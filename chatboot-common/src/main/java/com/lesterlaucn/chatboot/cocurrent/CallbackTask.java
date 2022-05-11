/**
 * Created by lesterlaucn
 */

package com.lesterlaucn.chatboot.cocurrent;


public interface CallbackTask<R> {

    R execute() throws Exception;

    void onBack(R r);

    void onException(Throwable t);
}
