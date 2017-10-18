/*
 * Copyright 2014, The Sporting Exchange Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.exemel.disco.transport.socket;

import uk.co.exemel.disco.netutil.nio.connected.InstallField;
import uk.co.exemel.disco.netutil.nio.connected.InstallIndex;
import uk.co.exemel.disco.netutil.nio.connected.InstallRoot;
import uk.co.exemel.disco.netutil.nio.connected.RemoveChildren;
import uk.co.exemel.disco.netutil.nio.connected.RemoveField;
import uk.co.exemel.disco.netutil.nio.connected.RemoveIndex;
import uk.co.exemel.disco.netutil.nio.connected.SetScalar;
import uk.co.exemel.disco.netutil.nio.connected.TerminateHeap;
import uk.co.exemel.disco.netutil.nio.connected.Update;
import uk.co.exemel.disco.netutil.nio.connected.UpdateAction;
import com.betfair.platform.virtualheap.HeapListener;
import com.betfair.platform.virtualheap.updates.UpdateBlock;

import java.util.ArrayList;
import java.util.List;

/**
 */
abstract class UpdateProducingHeapListener implements HeapListener {

    UpdateProducingHeapListener() {
    }

    @Override
    public void applyUpdate(UpdateBlock update) {
        List<UpdateAction> actions = new ArrayList<UpdateAction>();
        // Convert from heap representation to our wire representation
        for (com.betfair.platform.virtualheap.updates.Update u : update.list()) {
            switch (u.getUpdateType()) {
                case INSTALL_ROOT:
                    actions.add(new InstallRoot((com.betfair.platform.virtualheap.updates.InstallRoot) u));
                    break;
                case INSTALL_FIELD:
                    actions.add(new InstallField((com.betfair.platform.virtualheap.updates.InstallField) u));
                    break;
                case INSTALL_INDEX:
                    actions.add(new InstallIndex((com.betfair.platform.virtualheap.updates.InstallIndex) u));
                    break;
                case SET_SCALAR:
                    actions.add(new SetScalar((com.betfair.platform.virtualheap.updates.SetScalar) u));
                    break;
                case REMOVE_FIELD:
                    actions.add(new RemoveField((com.betfair.platform.virtualheap.updates.RemoveField) u));
                    break;
                case REMOVE_INDEX:
                    actions.add(new RemoveIndex((com.betfair.platform.virtualheap.updates.RemoveIndex) u));
                    break;
                case REMOVE_CHILDREN:
                    actions.add(new RemoveChildren((com.betfair.platform.virtualheap.updates.RemoveChildren) u));
                    break;
                case TERMINATE_HEAP:
                    actions.add(new TerminateHeap());
                    break;
                default:
                    throw new IllegalStateException("Unrecognised update type: "+u.getUpdateType());
            }
        }
        Update u = new Update();
        u.setActions(actions);
        doUpdate(u);
    }

    protected abstract void doUpdate(Update u);
}
