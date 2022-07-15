import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

class Seat {
    int number;
    char status;

    Seat(int number, char status) {
        this.number = number;
        this.status = status;
    }
}

class Ticket {

    ArrayList<Traveller> travellerArr;
    int nooftravellers;
    double totalfare;

    Ticket(ArrayList<Traveller> travellerArr, double totalfare) {
        this.travellerArr = travellerArr;
        nooftravellers = travellerArr.size();
        System.out.println("No of Travellers: " + nooftravellers);
        System.out.println("-----------------");
        for (Traveller t : travellerArr) {
            System.out.println("Name: " + t.name);
            System.out.println("Gender " + t.gender);
            System.out.println("Seat No." + t.seatno);
            System.out.println();
        }
        this.totalfare = totalfare;
        System.out.println("Total Fare: " + totalfare + "Rs.");
    }
}

class Bus {

    Scanner in = new Scanner(System.in);
    String bustype, seattype;
    int bookedseats, 
    totalseats = 12,
    noofcancellation;
    double fare;
    TreeMap<String, Ticket> tickets = new TreeMap<>();
    Seat[][] seatview = new Seat[4][3];
    TreeMap<String, Traveller> currTravellers = new TreeMap<>();
    TreeMap<String, ArrayList<Traveller>> selectedTravellers = new TreeMap<>();

    Bus(String bustype, String seattype, double fare) {
        this.bustype = bustype;
        this.seattype = seattype;
        this.fare = fare;
        int number = 1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                seatview[i][j] = new Seat(number, 'A');
                ++number;
            }
        }
    }

    void viewSeats() {
        System.out.println("\nBus Type: " + bustype.toUpperCase() + "\nSeat type: " + seattype.substring(0,1).toUpperCase() + seattype.substring(1) + "\nAvailable seats: " + totalseats + " seats\n");
        for (int i = 0; i < 4; i ++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(" " + seatview[i][j].number + ": " + seatview[i][j].status + " ");
            }
            System.out.println();
            if (i == 1) {
                System.out.println("==================");
            }
        }
        System.out.println("\nA: Availabe, M: Booked by man, W: Booked by woman.");
    }

    void getTravellers() {
        System.out.print("\nEnter number of travellers: ");
        int nooftravellers = in.nextInt();
        for (int i = 0; i < nooftravellers; i++) {
            System.out.println("Enter traveller name, gender and seatno as space seperated values.");
            String name = in.next();
            String gender = in.next().substring(0,1);
            int seatno = in.nextInt();
            currTravellers.put(gender+i, new Traveller(name, gender.charAt(0), seatno));
        }
    }

    void generateTickets(String user) {
        for (Map.Entry<String, ArrayList<Traveller>> s : selectedTravellers.entrySet()) { 
            if (s.getKey().equals(user)) {
                ArrayList<Traveller> travellerArr = s.getValue();
                int nooftravellers = travellerArr.size();
                double totalfare = nooftravellers * fare;
                Ticket t = new Ticket(travellerArr,totalfare);
                tickets.put(user, t);
                bookedseats += nooftravellers;
                totalseats -= nooftravellers;

                for (Traveller traveller : travellerArr) {
                    for (int i = 0; i < 4; i ++) {
                        for (int j = 0; j < 3; j++) {
                            if (traveller.seatno == seatview[i][j].number) {
                                seatview[i][j].status = traveller.gender;
                            }
                        }
                    }
                }
            }
        }
    }

    void bookTickets(Customer user) {
        ArrayList<Traveller> t = new ArrayList<>();
        for (Traveller c : currTravellers.values()) {
            System.out.println(c.name + ", " + c.gender);
            for (int i = 0; i < 4; i ++) {
                for (int j = 0; j < 3; j++) {
                    if (seatview[i][j].number == c.seatno && seatview[i][j].status == 'A') {
                        if (c.gender == 'W') {
                            // seatview[i][j].status = 'W';
                            t.add(c);
                            selectedTravellers.put(user.name, t);
                        }
                        else if (c.gender == 'M') {
                            if (((i+1)%2!=0 && seatview[i+1][j].status == 'W') || ((i+1)%2==0 && seatview[i-1][j].status == 'W')) {
                                System.out.println("Sorry , the seat " + seatview[i][j] + " is only for female.");
                            } else {
                                // seatview[i][j].status = 'M';
                                t.add(c);
                                selectedTravellers.put(user.name, t);
                            }
                        }
                    } else if (seatview[i][j].number == c.seatno && seatview[i][j].status != 'A') {
                        System.out.println("Sorry, the seat " + seatview[i][j] + " already booked.");
                    }
                    
                }
            }
        }
        // viewSeats();
        for (Map.Entry<String, ArrayList<Traveller>> s : selectedTravellers.entrySet()) {
            if (s.getKey().equals(user.name)) {
                System.out.println("Username: " + s.getKey() + "\nAvailable seats\n-----------------");
                for (Traveller t1 : s.getValue()) {
                    System.out.println(t1.name + ", "+t1.gender+ ", " +t1.seatno + " -> " + fare);
                }
            }
        }
        System.out.println("Are you ok with these tickets?Press Y for ok and any other key for no.");
        char isokwithtickets = in.next().charAt(0);
        if (String.valueOf(isokwithtickets).equalsIgnoreCase("Y")) {
            generateTickets(user.name);
            ++user.ticket;
            viewSeats();
        } else {
            selectedTravellers.remove(user.name);
        }
        currTravellers.clear();
    }
}

class Customer {
    String name, password, gender;
    int id, age, noofticket, ticket;


    Customer(int id, String name, String password, int age, String gender) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.age = age;
        this.gender = gender;
    }
}

class Traveller {
    String name;
    char gender;
    int seatno;
    
    Traveller(String name, char gender, int seatno) {
        this.name = name;
        this.gender = gender;
        this.seatno = seatno;
    }
}

public class Main {

    TreeMap<Integer, Bus> buses = new TreeMap<>();
    TreeMap<String, Customer> customers = new TreeMap<>();
    int userId = 4;
    Customer currUser;
    Scanner in = new Scanner(System.in);

    void loadInitialData() {
        //Initial Buses
        buses.put(1, new Bus("ac", "sleeper", 700));
        buses.put(2, new Bus("ac", "seater", 550));
        buses.put(3, new Bus("non-ac", "sleeper", 600));
        buses.put(4, new Bus("non-ac", "seater", 450));

        //Initial Customers
        customers.put("aaa", new Customer(1, "aaa", "111", 25, "F"));
        customers.put("bbb", new Customer(2, "bbb", "222", 61, "M"));
        customers.put("ccc", new Customer(3, "ccc", "333", 22, "M"));
        customers.put("ddd", new Customer(4, "ddd", "444", 36, "F"));
    }

    void signup(String name, String password, int age, String gender) {
        Customer existingCustomer = customers.get(name);
        if (existingCustomer != null) {
            System.out.println("\nCustomer with this username already exist.");
        } else {
            int id = ++userId;
            customers.put(name, new Customer(id, name, password, age, gender));
            System.out.println("\nSigned up successfully!");
        }
    }

    void showBuses() {
        Set<Map.Entry<Integer, Bus>> entries = buses.entrySet();

        Iterator<Map.Entry<Integer, Bus>> it = entries.iterator();
        int i = 1;
        while (it.hasNext()) {
            Map.Entry<Integer, Bus> entry = it.next();
            String bustype = entry.getValue().bustype;
            String seattype = entry.getValue().seattype;
            int seats = entry.getValue().totalseats;

            System.out.println( i + " - " + bustype.toUpperCase() + "-" + seattype + " - " + seats + " seats");
            ++i;
        }
    }

    void filterBuses() {
        ArrayList<String> busfilters = new ArrayList<>();
        ArrayList<String> seatfilters = new ArrayList<>();
        busfilters.add("ac");
        busfilters.add("non-ac");
        busfilters.add("both");

        seatfilters.add("sleeper");
        seatfilters.add("seater");
        seatfilters.add("both");

        String bustypefromuser, seattypefromuser;
        System.out.print("\nEnter bus type(ac/non-ac/both): ");
        bustypefromuser = in.next();
        System.out.println("Enter seat type(seater/sleeper/both): ");
        seattypefromuser = in.next();

        if (!busfilters.contains(bustypefromuser) || !seatfilters.contains(seattypefromuser)) {
            throw new InputMismatchException();
        }

        Set<Map.Entry<Integer, Bus>> entries = buses.entrySet();

        TreeMap<String, Bus> filteredBuses = new TreeMap<>();

        Iterator<Map.Entry<Integer, Bus>> it = entries.iterator();
        int i1 = 1;
        while (it.hasNext()) {
            Map.Entry<Integer, Bus> entry = it.next();
            String bustype = entry.getValue().bustype;
            String seattype = entry.getValue().seattype;
            int seats = entry.getValue().totalseats;
            int bookedseats = entry.getValue().bookedseats;

            if (seats > 0) {
                if ((bustypefromuser.equalsIgnoreCase("both") && seattypefromuser.equalsIgnoreCase("both")) || (bustypefromuser.equalsIgnoreCase(bustype) && seattypefromuser.equalsIgnoreCase(seattype)) || (bustypefromuser.equalsIgnoreCase(bustype) && seattypefromuser.equalsIgnoreCase("both")) || (bustypefromuser.equalsIgnoreCase("both") && seattypefromuser.equalsIgnoreCase(seattype))) {
                    filteredBuses.put(bookedseats+bustype+i1, entry.getValue());
                    ++i1;
                }
            }
        }
        int i = 1;
        for (Bus f: filteredBuses.values()) {
            System.out.println( i + " - " + f.bustype.toUpperCase() + "-" + f.seattype + " - " + f.totalseats + " seats");
            ++i;
        }
    }

    void booking() {
        System.out.println("\nSelect a bus number");
        showBuses();
        int choice = in.nextInt();
        if (choice > 4 || choice < 1) {
            System.out.println("\nSelect a correct number.");
        } else {
            Bus b = buses.get(choice);
            b.viewSeats();
            b.getTravellers();
            b.bookTickets(currUser);
        }
    }

    void summary() {
        Set<Map.Entry<Integer, Bus>> entries = buses.entrySet();

        Iterator<Map.Entry<Integer, Bus>> it = entries.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Bus> entry = it.next();
            String bustype = entry.getValue().bustype;
            String seattype = entry.getValue().seattype;
            int seats = entry.getValue().bookedseats;
            int cancels = entry.getValue().noofcancellation;
            double fare = entry.getValue().fare;
            int cancelfeeperct = 50;
            if (bustype.equalsIgnoreCase("ac")) {
                cancelfeeperct = 50;
            } else if (bustype.equalsIgnoreCase("non-ac")) {
                cancelfeeperct = 25;
            }
            double totalfare = seats * fare + cancels * fare * cancelfeeperct/100;

            System.out.println(bustype.toUpperCase() + " " + seattype + "\nNumber of seats filled: " + seats + "(" + seats + " tickets + " + cancels + " cancellation)" + "\nTotal Fare Collected: " + totalfare);
            System.out.println("----------------------------");
        }
    }

    double computeCancellationFee(Bus bus,int nooftravellers) {
        int cancelfeeperct = 50;
        if (bus.bustype.equalsIgnoreCase("ac")) {
            cancelfeeperct = 50;
        } else if (bus.bustype.equalsIgnoreCase("non-ac")) {
            cancelfeeperct = 25;
        }
        double cancellationfee = bus.fare * nooftravellers * cancelfeeperct/100;
        return cancellationfee;
    }

    void cancelAllTickets(Bus bus) {
        bus.bookedseats -= bus.tickets.get(currUser.name).nooftravellers;
        bus.totalseats += bus.tickets.get(currUser.name).nooftravellers;
        bus.noofcancellation += bus.tickets.get(currUser.name).nooftravellers;

        for (Traveller traveller : bus.tickets.get(currUser.name).travellerArr) {
            for (int i = 0; i < 4; i ++) {
                for (int j = 0; j < 3; j++) {
                    if (traveller.seatno == bus.seatview[i][j].number) {
                        bus.seatview[i][j].status = 'A';
                    }
                }
            }
        }
        double cancellationfee = computeCancellationFee(bus, bus.tickets.get(currUser.name).nooftravellers);
        bus.tickets.remove(currUser.name);
        --currUser.ticket;
        System.out.println("Your all tickets are cancelled. \nYour cancellation fee is " + cancellationfee);
    }

    void cancelSelectedTickets(Bus bus) {
        ArrayList<Traveller> travellers = bus.tickets.get(currUser.name).travellerArr;
        ArrayList<Traveller> travellers2 = new ArrayList<>();
        ArrayList<Integer> t = new ArrayList<>();
        for (Traveller trvlr : travellers) {
            int busseat = trvlr.seatno;
            t.add(busseat);
        }
        System.out.println("Enter the seat numbers you want to cancel as space separated");
        in.nextLine();
        String input = in.nextLine();
        String[] seats = input.split(" ");
        System.out.println(Arrays.toString(seats));
        int[] seatnos = new int[seats.length];
        for (int i = 0; i  < seats.length; i++) {
            int sno = Integer.parseInt(seats[i]);
            seatnos[i] = sno;
        }
        ArrayList<Integer> seatstobedeleted = new ArrayList<>();
        for (int s: seatnos) {
            if (t.contains(s)) {
                seatstobedeleted.add(s);
            }
        }

        if (seatstobedeleted.size() > 0) {
            bus.bookedseats -= seatstobedeleted.size();
            bus.totalseats += seatstobedeleted.size();
            bus.noofcancellation += seatstobedeleted.size();
    
            for (int a : seatstobedeleted) {
                for (int i = 0; i < 4; i ++) {
                    for (int j = 0; j < 3; j++) {
                        if (a == bus.seatview[i][j].number) {
                            bus.seatview[i][j].status = 'A';
                        }
                    }
                }
            }
        }
        for (Traveller trvlr2: travellers) {
            if (!(seatstobedeleted.contains(trvlr2.seatno))) {
                travellers2.add(trvlr2);
            }
        }
        travellers = travellers2;

        double cancellationfee = computeCancellationFee(bus, seatstobedeleted.size());

        if (travellers.size() < 1) {
            bus.tickets.remove(currUser.name);
            --currUser.ticket;
        }

        System.out.println("The tickets selected by you are deleted.\nYour cancellation fee is " + cancellationfee);
    }

    void cancelTicket() {
        Set<Map.Entry<Integer, Bus>> entries = buses.entrySet();

        Iterator<Map.Entry<Integer, Bus>> it = entries.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Bus> entry = it.next();
            Bus bus = entry.getValue();
            if (bus.tickets.containsKey(currUser.name)) {
                Ticket ticket = bus.tickets.get(currUser.name);
                System.out.println("\nYour booked seats:\n\nBus type: " + bus.bustype.toUpperCase() + "\nSeat type: " + bus.seattype + "\n------------------");
                for (Traveller t : ticket.travellerArr) {
                    System.out.println("\n" + t.name + " - " + t.gender + " - " + t.seatno);
                }
                System.out.println("\n\nWhich tickets do you want to cancel?\n1)All Tickets\n2)Selected tickets\nPress any other key to exit.");
                char choice = in.next().charAt(0);
                switch (choice) {
                    case '1':
                        cancelAllTickets(bus);
                        break;
                    case '2':
                        cancelSelectedTickets(bus);
                        break;
                    default:
                        break;
                }
                break;
            }
        }
    }

    void login(String name, String password) {
        Customer isCustomer = customers.get(name);
        if (isCustomer == null) {
            System.out.println("Please sign up as a new user.");
        } else if (isCustomer.password.equals(password)) {
            currUser = isCustomer;
            System.out.println("Loged in successfully.\n Welcome "+ currUser.name + "!");
            int t = 0;
            char choice;
            while(t==0) {
                if (currUser.name.equals("aaa")) {
                    System.out.println("\nSelect an option\n1)View Summary\nPress any key to logout");
                    choice = in.next().charAt(0);
                    switch (choice) {
                        case '1':
                            summary();
                            break;
                    
                        default:
                            System.out.println("Loged out successfully!");
                            ++t;
                            break;
                    }
                } else {
                    System.out.println("\nSelect an option\n-----------------------\n1)Show Buses\n2)Booking\n3)Cancel Ticket\n4)Filter Buses\nPress any other key to logout.");
                    choice = in.next().charAt(0);
                    switch (choice) {
                        case '1':
                            showBuses();
                            break;
                        
                        case '2':
                            if (currUser.ticket > 0) {
                                System.out.println("You already booked a ticket!");
                            } else {
                                booking();
                            }
                            break;

                        case '3':
                            cancelTicket();
                            break;
    
                        case '4':
                            filterBuses();
                            break;
                    
                        default:
                            System.out.println("Loged out successfully!");
                            ++t;
                            break;
                    }
                }
            }
        } else {
            System.out.println("Invalid credentials.");
        }
    }
    public static void main(String[] args) {
        try {
            Main m = new Main();
            m.loadInitialData();
            char choice;
            Scanner in = new Scanner(System.in);
            while(true) {
                System.out.println("\nSelect an option\n------------------\n1)Sign Up\n2)Log in\nPress any other key to exit.");
                String name;
                int age;
                String gender;
                String password;
                choice = in.next().charAt(0);
                switch (choice) {
                    case '1':
                        System.out.println("Enter username, age, gender(M/F) and password as space seperated values:");
                        name = in.next();
                        age = in.nextInt();
                        gender = in.next();
                        password = in.next();
                        m.signup(name, password, age, gender);
                        break;
    
                    case '2':
                        System.out.println("Enter username and password as space separated");
                        name = in.next();
                        password = in.next();
                        m.login(name, password);
                        break;
    
                    default:
                        in.close();
                        System.exit(0);
                        break;
                }
            }
        } catch(InputMismatchException e) {
            System.out.println("Check the input format.");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
