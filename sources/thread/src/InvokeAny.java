import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InvokeAny {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);
        ArrayList<Callable<Integer>> callables = Lists.newArrayList(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {

                System.out.println(1);
                return 1;
            }
        }, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println(2);
//                try {
//                    System.out.println(2);
//                    Thread.sleep(1000);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }

                return 2;
            }
        });
        Integer res = service.invokeAny(callables);
        System.out.println(res);

    }
}
