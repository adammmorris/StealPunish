package ethics.experiments.bimatrix;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Parallel
{
static final int iCPU = Runtime.getRuntime().availableProcessors()-1;

public static <T> void ForEach(Iterable <T> parameters,
                   final LoopBody<T> loopBody)
{
    ExecutorService executor = Executors.newFixedThreadPool(iCPU);
    //List<Future<?>> futures  = new LinkedList<Future<?>>();
    
    for (final T param : parameters)
    {
        executor.submit(new Runnable()
        {
            public void run() { loopBody.run(param); }
        });

    }

    /*for (Future<?> f : futures)
    {
        try   { f.get(); }
        catch (InterruptedException e) { } 
        catch (ExecutionException   e) { }         
    }*/

    executor.shutdown();     
}

public static void For(int start,
                   int stop,
               final LoopBody<Integer> loopBody)
{
    ExecutorService executor = Executors.newFixedThreadPool(iCPU);
    //List<Future<?>> futures  = new LinkedList<Future<?>>();
    
    for (int i=start; i<stop; i++)
    {
        final Integer k = i;
        executor.submit(new Runnable()
        {
            public void run() { loopBody.run(k); }
        });     
        //futures.add(future);
    }

    /*for (Future<?> f : futures)
    {
        try   { f.get(); }
        catch (InterruptedException e) { } 
        catch (ExecutionException   e) { }         
    }*/

    executor.shutdown();     
}

public static void withIndex(int start, int stop, final LoopBody<Integer> body) {
    int chunksize = (stop - start + iCPU - 1) / iCPU;
    int loops = (stop - start + chunksize - 1) / chunksize;
    ExecutorService executor = Executors.newFixedThreadPool(iCPU);
    final CountDownLatch latch = new CountDownLatch(loops);
    for (int i=start; i<stop;) {
        final int lo = i;
        i += chunksize;
        final int hi = (i<stop) ? i : stop;
        executor.submit(new Runnable() {
            public void run() {
                for (int i=lo; i<hi; i++)
                    body.run(i);
                latch.countDown();
            }
        });
    }
    try {
        latch.await();
    } catch (InterruptedException e) {}
    executor.shutdown();
}

}