package vn.hust.hustgame.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class EventDispatcherTest {

    private EventDispatcher dispatcher;

    @BeforeEach
    public void setUp() {
        EventDispatcher.resetInstance();
        dispatcher = EventDispatcher.getInstance();
    }

    @Test
    public void testDispatchEvent() {
        System.out.println("--- Bắt đầu test: testDispatchEvent ---");
        EventListener mockListener = Mockito.mock(EventListener.class);
        dispatcher.addListener(EventType.PLAYER_DIED, mockListener);

        GameEvent<String> event = new GameEvent<>(EventType.PLAYER_DIED, "Player 1");
        dispatcher.dispatch(event);

        verify(mockListener, times(1)).onEvent(event);
        System.out.println("Kết quả: Event đã được dispatch thành công tới Listener!");
        System.out.println("--- Kết thúc test: testDispatchEvent ---\n");
    }

    @Test
    public void testSafeIterationDuringDispatch() {
        System.out.println("--- Bắt đầu test: testSafeIterationDuringDispatch ---");
        EventListener mockListener1 = Mockito.mock(EventListener.class);
        EventListener mockListener2 = Mockito.mock(EventListener.class);

        // Listener 1 will remove Listener 2 when an event is received
        // This tests the `isDispatching` flag and `queueActions` list
        doAnswer(invocation -> {
            System.out.println("  -> Listener 1 nhận event, chuẩn bị xoá Listener 2");
            dispatcher.removeListener(EventType.PLAYER_DIED, mockListener2);
            return null;
        }).when(mockListener1).onEvent(any());

        dispatcher.addListener(EventType.PLAYER_DIED, mockListener1);
        dispatcher.addListener(EventType.PLAYER_DIED, mockListener2);

        GameEvent<String> event = new GameEvent<>(EventType.PLAYER_DIED, "Player 1");
        
        System.out.println("  Gửi event lần 1...");
        // Should not throw ConcurrentModificationException
        dispatcher.dispatch(event);
        
        verify(mockListener1, times(1)).onEvent(event);
        verify(mockListener2, times(1)).onEvent(event);
        System.out.println("  Cả Listener 1 và 2 đều nhận được event lần 1 an toàn.");

        System.out.println("  Gửi event lần 2...");
        // Next dispatch should not notify listener 2
        dispatcher.dispatch(event);
        verify(mockListener1, times(2)).onEvent(event);
        verify(mockListener2, times(1)).onEvent(event);
        
        System.out.println("Kết quả: Việc xoá Listener trong khi dispatch được xử lý an toàn (Queue Action)!");
        System.out.println("--- Kết thúc test: testSafeIterationDuringDispatch ---\n");
    }
}
