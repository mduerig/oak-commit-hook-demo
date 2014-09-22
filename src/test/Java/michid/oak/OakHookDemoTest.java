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

    @Before
    public void setup() {
        repository = new Jcr()
            .with(new OakHookDemo())
            .createRepository();
    }

    @After
    public void tearDown() {
        if (repository instanceof JackrabbitRepository) {
            ((JackrabbitRepository) repository).shutdown();
        }
    }

    @Test
    public void test() throws RepositoryException {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        session.getRootNode().addNode("new node " + System.currentTimeMillis());
        session.save();
        session.logout();
    }

}
