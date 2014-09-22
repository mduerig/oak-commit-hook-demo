/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package michid.oak;

import static org.apache.jackrabbit.oak.commons.PathUtils.concat;

import org.apache.jackrabbit.oak.api.CommitFailedException;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.jackrabbit.oak.spi.commit.CommitHook;
import org.apache.jackrabbit.oak.spi.commit.CommitInfo;
import org.apache.jackrabbit.oak.spi.commit.DefaultEditor;
import org.apache.jackrabbit.oak.spi.commit.Editor;
import org.apache.jackrabbit.oak.spi.commit.EditorDiff;
import org.apache.jackrabbit.oak.spi.state.NodeBuilder;
import org.apache.jackrabbit.oak.spi.state.NodeState;

public class OakHookDemo implements CommitHook {

    private final String path;

    public OakHookDemo(String path) {
        this.path = path;
    }

    @Override
    public NodeState processCommit(NodeState before, NodeState after, CommitInfo info)
            throws CommitFailedException {

        NodeBuilder builder = after.builder();
        Editor editor = new DemoEditor(builder);
        CommitFailedException exception = EditorDiff.process(editor, before, after);
        if (exception == null) {
            return builder.getNodeState();
        } else {
            throw exception;
        }
    }

    private class DemoEditor extends DefaultEditor {
        private final String path;
        private final NodeBuilder builder;

        public DemoEditor(String path, NodeBuilder builder) {
            this.path = path;
            this.builder = builder;
        }

        public DemoEditor(NodeBuilder builder) {
            this("/", builder);
        }

        @Override
        public Editor childNodeAdded(String name, NodeState after) throws CommitFailedException {
            if (PathUtils.isAncestor(OakHookDemo.this.path, concat(path, name))) {
                builder.child(name).setProperty("added_at", System.currentTimeMillis());
            }
            return new DemoEditor(concat(path, name), builder.child(name));
        }

        @Override
        public Editor childNodeChanged(String name, NodeState before, NodeState after) throws CommitFailedException {
            return new DemoEditor(concat(path, name), builder.child(name));
        }
    }
}
