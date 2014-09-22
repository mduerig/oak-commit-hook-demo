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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OakHookDemoTest {

    private Repository repository;
    private Session session;
    private Node root;

    @Before
    public void setup() throws RepositoryException {
        repository = new Jcr()
            .with(new OakHookDemo("/foo/bar"))
            .createRepository();
        session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        root = session.getRootNode();
    }

    @After
    public void tearDown() {
        session.logout();
        if (repository instanceof JackrabbitRepository) {
            ((JackrabbitRepository) repository).shutdown();
        }
    }

    @Test
    public void testExtraContent() throws RepositoryException {
        root.addNode("foo").addNode("bar").addNode("two");
        session.save();

        assertFalse(root.getNode("foo").hasProperty("added_at"));
        assertFalse(root.getNode("foo/bar").hasProperty("added_at"));
        assertTrue(root.getNode("foo/bar/two").hasProperty("added_at"));
    }

    @Test
    public void testNoExtraContent() throws RepositoryException {
        root.addNode("foo").addNode("baz").addNode("two");
        session.save();

        assertFalse(root.getNode("foo").hasProperty("added_at"));
        assertFalse(root.getNode("foo/baz").hasProperty("added_at"));
        assertFalse(root.getNode("foo/baz/two").hasProperty("added_at"));
    }

}
