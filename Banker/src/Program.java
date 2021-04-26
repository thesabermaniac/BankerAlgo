import java.util.ArrayList;
import java.util.Random;

public class Program
{

    final static int NUM_PROCS = 6; // How many concurrent processes
    final static int TOTAL_RESOURCES = 30; // Total resources in the system
    final static int MAX_PROC_RESOURCES = 13; // Highest amount of resources any process could need
    final static int ITERATIONS = 30; // How long to run the program
    static int totalHeldResources = 0; // How many resources are hel by all processes combined
    static Random rand = new Random();

    /**
     * Helper method that implements Dijkstra's Banker algorithm to determine whether
     * or not a processes request will lead to an unsafe state
     *
     * @param processes - ArrayList of processes currently in the OS
     * @param availableResources - Total number of resources available to the OS
     * @param currProc - Process that is currently making a request
     * @param currRequest - Number of resources requested by process
     * @return - true if request is safe
     */
    public static boolean isSafe(ArrayList<Proc> processes, int availableResources, int currProc, int currRequest){
        // Create a deep copy of the processes ArrayList to avoid changing the current processes during simulation
        ArrayList<Proc> procs = new ArrayList<>();
        for(Proc p : processes){
            Proc newProc = new Proc(p.getMaxResources(), p.getID());
            newProc.addResources(p.getHeldResources());
            procs.add(newProc);
        }

        // Simulate what would happen if the current request was granted
        procs.get(currProc).addResources(currRequest);
        availableResources -= currRequest;

        // Dijkstra's Banker Algorithm. This determines whether or not the other processes would
        // have sufficient resources available to them if this request was granted
        while (!procs.isEmpty()){
            boolean found = false;
            for (int i = procs.size() - 1; i >=0; i--){
                if((procs.get(i).getMaxResources() - procs.get(i).getHeldResources()) <= availableResources){
                    availableResources = availableResources + procs.get(i).getHeldResources();
                    procs.remove(procs.get(i));
                    found = true;
                }
            }
            if(!found)
                return false;
        }
        return true;

    }

    public static void main(String[] args)
    {

        // The list of processes:
        ArrayList<Proc> processes = new ArrayList<Proc>();
        for (int i = 0; i < NUM_PROCS; i++)
            processes.add(new Proc(MAX_PROC_RESOURCES - rand.nextInt(3), i)); // Initialize to a new Proc, with some small range for its max

        // Run the simulation:
        for (int i = 0; i < ITERATIONS; i++)
        {
            // loop through the processes and for each one get its request
            // I changed this to go in reverse order so I can remove processes when they complete
            for (int j = processes.size() - 1; j >= 0; j--)
            {
                // Get the request
                int currRequest = processes.get(j).resourceRequest(TOTAL_RESOURCES - totalHeldResources);

                // just ignore processes that don't ask for resources
                if (currRequest == 0)
                    continue;

                // Here you have to enter code to determine whether or not the request can be granted,
                // and then grant the request if possible. Remember to give output to the console
                // this indicates what the request is, and whether or not its granted.

                // update availableResources to reflect any requests that have been granted
                int availableResources = TOTAL_RESOURCES - totalHeldResources;

                // handle completed processes by returning the resources and removing the process from the ArrayList
                if(currRequest < 0){
                    System.out.println("Process " + processes.get(j).getID() + " completed, returned " + Math.abs(currRequest));
                    totalHeldResources += currRequest;
                    processes.remove(j);
                }
                else {
                    System.out.print("Process " + processes.get(j).getID() + " requested " + currRequest);

                    // delegate safe check to the Dijkstra's Banker Algorithm helper method
                    if (isSafe(processes, availableResources, j, currRequest)) {
                        // update resources if request is safe
                        processes.get(j).addResources(currRequest);
                        totalHeldResources += currRequest;
                        System.out.println(", granted");
                    } else
                        System.out.println(", denied");
                }

                // At the end of each iteration, give a summary of the current status:
                System.out.println("\n***** STATUS *****");
                System.out.println("Total Available: " + (TOTAL_RESOURCES - totalHeldResources));
                for (int k = 0; k < processes.size(); k++)
                    System.out.println("Process " + processes.get(k).getID() + " holds: " + processes.get(k).getHeldResources() + ", max: " +
                            processes.get(k).getMaxResources() + ", claim: " +
                            (processes.get(k).getMaxResources() - processes.get(k).getHeldResources()));
                System.out.println("***** STATUS *****\n");

            }

            // end for loop early if all processes are completed
            if(processes.isEmpty()){
                System.out.println("***** ALL PROCESSES COMPLETED *****");
                break;
            }
        }

    }

}
