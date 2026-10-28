package my.dating.app;

import my.dating.app.controller.ProfileController;
import my.dating.app.controller.SearchProfileController;
import my.dating.app.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

// SolarFramework registers a TransactionManager bean only from inside a transaction that is already bound
// (DatabaseService.getJpaBeans, called by TransactionalAccess), so a @Transactional method can never start one:
// Spring finds no TransactionManager and the request fails with a 500. Its own writes don't join a Spring
// transaction either. Until the framework offers one, these classes must not use the annotation.
class TransactionalUsageTest {

    @Test
    void writingClassesDontUseTransactional() {
        for (Class<?> type : new Class<?>[] {ProfileController.class, SearchProfileController.class, ChatService.class}) {
            long annotated = Arrays.stream(type.getDeclaredMethods()).filter((Method m) -> m.isAnnotationPresent(Transactional.class)).count();
            assertEquals(0, annotated, type.getSimpleName());
        }
    }
}
