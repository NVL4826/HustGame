import com.badlogic.gdx.utils.PoolManager;
import java.lang.reflect.Method;

public class DiscoverPoolManager {
    public static void main(String[] args) {
        try {
            Class<?> clazz = PoolManager.class;
            System.out.println("Methods in PoolManager:");
            for (Method m : clazz.getDeclaredMethods()) {
                System.out.print(m.getName() + "(");
                Class<?>[] params = m.getParameterTypes();
                for (int i = 0; i < params.length; i++) {
                    System.out.print(params[i].getSimpleName());
                    if (i < params.length - 1) System.out.print(", ");
                }
                System.out.println(")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
