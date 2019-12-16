public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
public class ServiceSystem {
    private static final int NUMBER_OF_CPU = 2;
    private static final int NUMBER_OF_FLOWS = 2;
    private static final int NUMBER_OF_QUEUE = 1;
    private static final int NUMBER_OF_PROCESS = 15;

    private final CPU[] cpus;
    private final ProcessFlow[] flows;
    private final CPUQueue[] queues;
    private final Thread[] flowThreads;
    private final Thread[] cpuThreads;
    private int processLost;
    private int processInterrupted;
    private int processes;

    /**
     * Constructor of Service System
     *
     * @param nCPU       number of CPUs in system
     * @param nFlow      number of Process Flows in system
     * @param nQueue     number of Concurrent Process Queues in system
     * @param nProcesses number of Processes to generate by each Process Flow
     */
    public ServiceSystem(int nCPU, int nFlow, int nQueue, int nProcesses) {
        if (nCPU <= 0 || nFlow <= 0 || nQueue <= 0 || nProcesses <= 0) {
            throw new IllegalArgumentException();
        }

        cpus = new CPU[nCPU];
        cpuThreads = new Thread[nCPU];
        for (int i = 0; i < nCPU; i++) {
            cpus[i] = new CPU();                    // runnable object
            cpuThreads[i] = new Thread(cpus[i]);    // flow for this object
        }

        flows = new ProcessFlow[nFlow];
        flowThreads = new Thread[nFlow];
        for (int i = 0; i < nFlow; i++) {
            flows[i] = new ProcessFlow(nProcesses);  // runnable object
            flowThreads[i] = new Thread(flows[i]);   // thread for this object
        }

        this.queues = new CPUQueue[nQueue];
        for (int i = 0; i < nQueue; i++) {
            queues[i] = new CPUQueue();
        }
        processes = nProcesses * 2; // all processes
        processInterrupted = 0;
        processLost = 0;

    }

    /**
     * Runs CPU and Process Flow threads
     * Attention:  ProcessFlow should set queue before run
     */
    private void runThreads() {
        for (int i = 0; i < cpuThreads.length; i++) {
            cpuThreads[i].start();
        }

        for (int i = 0; i < flowThreads.length; i++) {
            flowThreads[i].start();
        }

    }

    /**
     * Returns true if any Process Flow is Alive
     *
     * @return true if any Process Flow is Alive
     */

    private boolean isAlive() {
        for (int i = 0; i < flowThreads.length; i++) {
            if (flowThreads[i].isAlive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Constructor of Service System
     * NUMBER_OF_CPU  number of CPUs
     * NUMBER_OF_FLOWS number of Process Flows
     * NUMBER_OF_QUEUE number of Concurrent Process Queues
     *
     * @param nProcesses number of Processes to generate by each Process Flow
     */
    public ServiceSystem(int nProcesses) {
        this(NUMBER_OF_CPU, NUMBER_OF_FLOWS, NUMBER_OF_QUEUE, nProcesses);
    }


    private static boolean isAnyFlowAilive(ServiceSystem ss) {
        for (int i = 0; i < ss.flowThreads.length; i++) {
            if (ss.flowThreads[i].isAlive()) {
                return true;
            }
        }
        return false;
    }
    private static boolean isAnyCPUAilive(ServiceSystem ss) {
        for (int i = 0; i < ss.cpuThreads.length; i++) {
            if (ss.cpuThreads[i].isAlive()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Simulates of Service System
     * CPU threads              2
     * ProcessFlow threads      2
     * Queue                    1
     * Run main system loop
     *
     * @param args
     */
    public static void main(String[] args) {
        ServiceSystem ss = new ServiceSystem(NUMBER_OF_PROCESS);  //  by default generate 25 process by each flow
// вариант 9 одна очередь на все потоки
        for (int i = 0; i < ss.flows.length; i++) {
            ss.flows[i].setQueue(ss.queues[0]);
        }
// запуск всех потоков в работу
        for (int i = 0; i < ss.cpuThreads.length; i++) {
            ss.cpuThreads[i].start();
        }

        for (int i = 0; i < ss.flowThreads.length; i++) {
            ss.flowThreads[i].start();
        }


// собственно цикл обработки процессов
        Process p;
        CPU cpu0 = ss.cpus[0];  // просто для читабельности
        CPU cpu1 = ss.cpus[1];  // просто для читабельности
        ProcessFlow flow1 = ss.flows[0];
        ProcessFlow flow2 = ss.flows[1];
        CPUQueue queue = ss.queues[0];

        while (ss.isAlive() || !queue.isEmpty()) {
            if (!queue.isEmpty()) {
                p = queue.remove();                  // взять очередной процесс
                if (p.getFlow() == flow1.getId()) {     // если это процесс первого потока
                    if (!cpu0.isBusy()) {
                        cpu0.setTask(p);
                        System.out.println(p + " взят на обработку " + cpu0);
                    } else {
                        System.out.println(p + " будет прерывать " + cpu0 + " " + cpu0.isBusy() + " " + cpu0.getProcess());
                        if (cpu0.getProcess().getFlow() == flow2.getId()) { // cpu0 занят чужим потоком надо прерывать
                            ss.cpuThreads[0].interrupt();                   // прервали
                            while (cpu0.isBusy()) {                         // waiting for Lost
                                System.out.print(".");
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            System.out.println("**** Interruption sent ***");
                            Process lost = cpu0.getLost();
                            if (lost != null) {                               // сохраняем все потерянные потоки
                                System.out.println("*** Lost received ***");
                                queue.add(cpu0.getLost());
                                cpu0.setLost(null);                         // clear lost
                                System.out.println(p + " поставили в очередь из " + cpu0);
                                System.out.println(queue);
                                ss.processInterrupted++;
                            } else {
                                System.out.println("**** Interruption Lost ***");
                                throw new IllegalArgumentException();
                            }
                            while (cpu0.isBusy()) {                          // ждем пока не завершит прерывание
                            }
                            cpu0.setTask(p);
                            System.out.println(p + " взят на обработку " + cpu0);
                        } else {                                            // cpu0 занят своим потоком
                            System.out.println(p + " в обработке отказано " + cpu0 + " " + cpu0.isBusy());
                            if (!cpu1.isBusy()) {
                                cpu1.setTask(p);
                                System.out.println(p + " взят на обработку " + cpu1);
                            } else {                                        // cpu1 тоже занят
                                System.out.println(p + " в обработке отказано процесс забыть " + cpu1 + " " + cpu1.isBusy());
                                ss.processLost++;
                            }
                        }
                    }
                }
                if (p.getFlow() == flow2.getId()) {     // если это процесс второго потока
                    if (!cpu1.isBusy()) {
                        cpu1.setTask(p);
                        System.out.println(p + " взят на обработку " + cpu1);
                    } else {
                        if (!cpu0.isBusy()) {  // пытаемся второй поток поставить в первый процессор
                            cpu0.setTask(p);
                            System.out.println(p + " взят на обработку " + cpu0);
                        } else {                    // никто поток не взял
                            System.out.println(p + " отказано поставить в очередь " + cpu1 + " " + cpu1.isBusy() + " " + cpu0 + " " + cpu0.isBusy());  // если н
                            queue.add(p);
                        }
                    }
                }

                System.out.println("alive " + queue);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {

                }
            }
        } // while

        System.out.println("Обработка завершена");
        while(isAnyCPUAilive(ss)) {
            for (int i = 0; i < ss.cpuThreads.length; i++) {
                if(!ss.cpus[0].isBusy()) {
                    ss.cpuThreads[0].interrupt();
                }
                if(!ss.cpus[1].isBusy()) {
                    ss.cpuThreads[1].interrupt();
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {

            }
        }

        System.out.println("Результаты обработки:");
        System.out.println("Процессов всего: "+ss.processes);
        System.out.println("Процессов уничтоженных 1го потока: "+ss.processLost+
                String.format("  %.1f%%",100*(double)ss.processLost/ss.processes));
        System.out.println("Процессов прерванных   2го потока: "+ss.processInterrupted+
                String.format("  %.1f%%",100*(double)ss.processInterrupted/ss.processes));
        System.out.println("Максимальный размер очереди: "+ss.queues[0].getMaxSize());

    }
}