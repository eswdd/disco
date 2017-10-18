/*
 * Copyright 2015, Simon Matić Langford
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

package uk.co.exemel.disco.test.socket.tester.server;

import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.core.api.ev.*;
import uk.co.exemel.disco.core.impl.ev.ConnectedResponseImpl;
import uk.co.exemel.disco.core.impl.ev.DefaultSubscription;
import uk.co.exemel.disco.test.socket.tester.common.*;
import com.betfair.platform.virtualheap.*;

import java.util.concurrent.ConcurrentHashMap;

/**
*
*/
class HeapOperation extends ClientAuthExecutable {

    private ConcurrentHashMap<String, HeapAndSub> heapsAndSubs = new ConcurrentHashMap<>();

    HeapOperation(boolean needsClientAuth) {
        super(needsClientAuth);
    }

    @Override
    public void execute(ExecutionContext ctx, OperationKey key, Object[] args, ExecutionObserver observer, ExecutionVenue executionVenue, TimeConstraints timeConstraints) {
        if (key.equals(Common.heapSubscribeOperationDefinition.getOperationKey())) {
            if (!checkClientAuth(ctx,observer))
            {
                return;
            }
            String clientId = (String) args[0];

            DefaultSubscription sub = new DefaultSubscription();
            Heap heap = new MutableHeap("fred-"+clientId);
            HeapAndSub hs = new HeapAndSub(heap, sub);
            heapsAndSubs.put(clientId,hs);
            ConnectedResponseImpl response = new ConnectedResponseImpl(heap, sub);
            observer.onResult(new ExecutionResult(response));
        }
        else if (key.equals(Common.heapSetOperationDefinition.getOperationKey())) {
            String clientId = (String) args[0];
            HeapAndSub hs = heapsAndSubs.get(clientId);

            Heap heap = hs.getHeap();
            heap.beginUpdate();
            MapNode root = (MapNode) heap.ensureRoot(NodeType.MAP);
            ScalarNode node = (ScalarNode) root.ensureField("message",NodeType.SCALAR);
            node.set(args[1]);

            heap.endUpdate();
            observer.onResult(new ExecutionResult());
        }
        else if (key.equals(Common.heapCloseOperationDefinition.getOperationKey())) {
            String clientId = (String) args[0];
            HeapAndSub hs = heapsAndSubs.remove(clientId);

            hs.getHeap().beginUpdate();
            hs.getHeap().terminateHeap();
            hs.getHeap().endUpdate();
            observer.onResult(new ExecutionResult());
        }
    }

    private class HeapAndSub
    {
        private Heap heap;
        private Subscription sub;

        private HeapAndSub(Heap heap, Subscription sub) {
            this.heap = heap;
            this.sub = sub;
        }

        private Heap getHeap() {
            return heap;
        }

        private Subscription getSub() {
            return sub;
        }
    }
}
