package com.march.socialsdk.workflow;


/**
 * CreateAt : 2018/8/11
 * Describe :
 *
 * @author chendong
 */
public interface TaskAction<P, R> {
    R call( P param);
}
