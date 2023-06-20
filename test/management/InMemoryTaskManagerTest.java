package management;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void startTheProcess() {
        manager = new InMemoryTaskManager();
    }

}