package com.march.socialsdk.workflow;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CreateAt : 2018/8/11
 * Describe :
 *
 * @author chendong
 */
public class Task<TResult> {

    public static final int UI        = 0;
    public static final int BG        = 1;
    public static final int IMMEDIATE = 2;

    // 任务是否已经执行
    private boolean     alreadyCall;
    // 任务名称
    private int         taskIndex;
    // 上一个任务节点
    private Task        preNode;
    // 下一个任务节点
    private Task        nextNode;
    // 当前任务节点
    private Task        curNode;
    // 当前节点的任务内容
    private TaskAction  taskAction;
    // 错误处理
    private ErrorAction errorAction;
    // 线程模式
    private int threadMode = UI;

    // 是否 UI 线程
    private static boolean isUI() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    // UI 线程处理者
    private static Poster uiPoster = new Poster() {
        Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void post(Runnable runnable) {
            if (isUI()) {
                runnable.run();
            } else {
                handler.post(runnable);
            }
        }
    };

    // 子线程处理者
    private static Poster bgPoster = new Poster() {

        ExecutorService service = Executors.newCachedThreadPool();

        @Override
        public void post(Runnable runnable) {
            if (isUI()) {
                service.execute(runnable);
            } else {
                runnable.run();
            }
        }
    };

    // 初始化参数
    private void init(int threadMode, Task preNode, Task curNode) {
        if (preNode == null) {
            this.taskIndex = 0;
        } else {
            this.taskIndex = preNode.taskIndex + 1;
        }
        this.threadMode = threadMode;
        this.preNode = preNode;
        this.curNode = curNode;
        this.nextNode = null;
    }

    // 在当前线程启动任务链
    public static <TResult> Task<TResult> call( CallAction<TResult> action) {
        return call(IMMEDIATE, action);
    }

    // 在指定启动任务链
    public static <TResult> Task<TResult> call(int threadMode,final CallAction<TResult> action) {
        Task<TResult> taskNode = new Task<>();
        taskNode.init(threadMode, null, taskNode);
        taskNode.taskAction = new TaskAction<Void, TResult>() {
            
            @Override
            public TResult call( Void param) {
                return action.call();
            }
        };
        return taskNode;
    }

    // 在当前线程追加一个任务
    public <ThenResult> Task<ThenResult> then( TaskAction<TResult, ThenResult> action) {
        return then(IMMEDIATE, action);
    }

    // 在指定线程追加一个任务
    public <ThenResult> Task<ThenResult> then(int threadMode,TaskAction<TResult, ThenResult> action) {
        if (this.errorAction != null) {
            throw new IllegalStateException("error task must be last");
        }
        Task<ThenResult> taskNode = new Task<>();
        curNode.nextNode = taskNode;
        taskNode.init(threadMode, curNode, taskNode);
        taskNode.taskAction = action;
        return taskNode;
    }

    // 在 UI 线程统一错误处理，注意这里的区别：
    // 因为发生错误的任务线程不定，所以没办法在上游线程继续错误处理，因此默认到 UI 线程
    public Task error( ErrorAction action) {
        return error(UI, action);
    }

    // 在指定线程进行错误处理
    public Task error(int threadMode,ErrorAction action) {
        Task errorNode = new Task<>();
        errorNode.init(threadMode, curNode, errorNode);
        this.errorAction = action;
        return errorNode;
    }

    // 执行任务，调用该方法后，任务链开始执行
    public void execute() {
        // 找到第一个任务
        Task firstTaskNode = findFirstTaskNode();
        firstTaskNode.runTaskAction(null);
    }

    // 调用任务内容
    @SuppressWarnings("unchecked")
    private void runTaskAction(final Object param) {
        if (alreadyCall) {
            throw new IllegalStateException("task already call before, created again!");
        }
        if (taskAction == null)
            return;
        alreadyCall = true;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    TResult result = (TResult) taskAction.call(param);
                    if (nextNode != null) {
                        nextNode.runTaskAction(result);
                    }
                } catch (Exception ex) {
                    runErrorAction(ex);
                }
            }
        };
        if (threadMode == IMMEDIATE) {
            runnable.run();
        } else {
            Poster poster = threadMode == UI ? uiPoster : bgPoster;
            poster.post(runnable);
        }
    }

    // 调用错误回调
    private void runErrorAction(final Exception ex) {
        final Task lastTaskNode = findLastTaskNode();
        if (lastTaskNode == null || lastTaskNode.errorAction == null) {
            return;
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                lastTaskNode.errorAction.error(ex);
            }
        };
        if (threadMode == IMMEDIATE) {
            r.run();
        } else {
            Poster poster = lastTaskNode.threadMode == UI ? uiPoster : bgPoster;
            poster.post(r);
        }
    }

    // 从后往前列出所有任务节点
    public String listTaskNodes() {
        StringBuilder sb = new StringBuilder();
        Task node = curNode;
        while (node != null) {
            sb.append("taskIndex = ").append(node.taskIndex).append("\n");
            node = node.preNode;
        }
        return sb.toString();
    }

    interface Poster {
        void post(Runnable runnable);
    }

    private Task findFirstTaskNode() {
        Task node = curNode;
        while (node.preNode != null) {
            node = node.preNode;
        }
        return node;
    }

    private Task findLastTaskNode() {
        Task node = curNode;
        while (node.nextNode != null) {
            node = node.nextNode;
        }
        return node;
    }
}
