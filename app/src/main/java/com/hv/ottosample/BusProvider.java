/*
 * Copyright (C) 2012 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hv.ottosample;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Maintains a singleton instance for obtaining the bus. Ideally this would be replaced with a more efficient means
 * such as through injection directly into interested classes.
 */
public class BusProvider {
  private static final Bus bus = new MainThreadBus();

  public static Bus getInstance(){
    return bus;
  }

  private BusProvider(){
    //Don't allow public instantiation
  }

  /**
   * Be able to post from any thread to main thread
   */
  public static class MainThreadBus extends Bus {
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override public void post(final Object event) {
      if (Looper.myLooper() == Looper.getMainLooper()) {
        super.post(event);
      } else {
        handler.post(new Runnable() {
          @Override
          public void run() {
            MainThreadBus.super.post(event);
          }
        });
      }
    }
  }
}
