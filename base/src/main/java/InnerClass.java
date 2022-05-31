public class InnerClass {


    public static void main(String[] args) {
        service ss = new service(){
            @Override
            public void say(String ss) {
                ss = "daged,"+ss;
                System.out.println(ss);
            }
        };
        ss.say("xxxxxxxxxxxxxxxxx");
    }

    interface service{
        void say(String ss);
    }

//    class Service implements service{
//
//        @Override
//        public void say(String ss) {
//            ss = "Service," + ss;
//            System.out.println(ss);
//        }
//    }
}
