package cn.tangaqi;

import cn.classfile.resultClass;
import cn.classfile.tableClass;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Scanner;

/**
 * Created by ZNing on 2017/6/13.
 */

public class ERPConsole {

    static tableClass tc = new tableClass();
    static resultClass rc = new resultClass();

    public static void ReadTable(){
        Scanner sc = new Scanner(System.in);
        System.out.println("程序启动正常\n请输入物料号");
        tc.MaterId = sc.nextInt();
        System.out.println("请输入物料名称");
        tc.MaterName = sc.next();
        System.out.println("请输入计划日期");
        tc.PlannedTime = sc.next();
        System.out.println("请输入计划员");
        tc.PlannedPeer = sc.next();
        System.out.println("请输入现有库存量");
        tc.ExistingStock = sc.nextInt();
        System.out.println("请输入安全库存量");
        tc.SafetyStock = sc.nextInt();
        System.out.println("请输入批量");
        tc.BatchVol = sc.nextInt();
        System.out.println("请输入批量增量");
        tc.BatchIncrement = sc.nextInt();
        System.out.println("请输入提前期");
        tc.LeadTime = sc.nextInt();
        System.out.println("请输入需求时界");
        tc.NeededTimeEdge = sc.nextInt();
        System.out.println("请输入计划时界");
        tc.PlannedTimeEdge = sc.nextInt();
        //输入预测值和合同量
        sc.nextLine();//吸收nextInt的回车
        System.out.println("请输入预测值11个，以空格分割");
        String s1 = sc.nextLine();//将用户输入的一整行字符串赋给s
        String[] c1 = s1.split("\\s+");//用空格将其分割成字符串数组
        for(int i = 0; i < 11; i++) {
            tc.ForecastVol[i] = Integer.parseInt(c1[i]);//讲字符串数组转换成int数组
        }
        //sc.nextLine();//吸收上一个next的回车
        System.out.println("请输入合同值11个，以空格分割");
        String s2 = sc.nextLine();//将用户输入的一整行字符串赋给s
        //System.out.println(s2);
        String[] c2 = s2.split("\\s+");//用空格将其分割成字符串数组
        for(int i = 0; i < 11; i++) {
            tc.ContractVol[i] = Integer.parseInt(c2[i]);//讲字符串数组转换成int数组
        }
        sc.close();
    }
    // 计算毛需求,通过
    public static void Process_GrossDemand(){
        for(int i = 0; i < 3; i++){
            rc.GrossDemand[i] = tc.ContractVol[i];
        }
        for(int i = 3; i < 8; i++){
            rc.GrossDemand[i] = Math.max(tc.ForecastVol[i],tc.ContractVol[i]);
        }
        for(int i = 8; i < 11; i++){
            rc.GrossDemand[i] = tc.ForecastVol[i];
        }
    }

    // 计算计划接收量、预计库存量,通过
    public static void Process_PlannedReceipt_And_EstimatedInventory(){
        // 第一周
        rc.PlannedReceipt[0]=tc.BatchVol;
        rc.EstimatedInventory[0]=tc.ExistingStock+rc.PlannedReceipt[0]-rc.GrossDemand[0];
        // 第二周及以后
        for (int i = 1; i<11; i++){
            int temp = rc.EstimatedInventory[i-1]-tc.SafetyStock;
            if(temp < rc.GrossDemand[i])
            {
                int total = tc.BatchVol;
                //System.out.print(temp+"\n");
                while(total<rc.GrossDemand[i]){
                    total += tc.BatchIncrement;
                }
                rc.PlannedReceipt[i]=total;
            }
            else
            {
                rc.PlannedReceipt[i]=0;
            }
            rc.EstimatedInventory[i]=rc.EstimatedInventory[i-1]+rc.PlannedReceipt[i]-rc.GrossDemand[i];
        }
    }

    // 计算净需求:毛需求+安全库存-计划接受-（前一个的）预计库存,通过
    public static void Process_NetDemand(){
        // 第一周
        rc.NetDemand[0]=rc.GrossDemand[0]+tc.SafetyStock-rc.PlannedReceipt[0]-tc.ExistingStock;
        if(rc.NetDemand[0]<0)
            rc.NetDemand[0]=0;
        // 第二周及以后
        for (int i = 1; i<11; i++){
            rc.NetDemand[i]=rc.GrossDemand[i]+tc.SafetyStock-0-rc.EstimatedInventory[i-1];
            if(rc.NetDemand[i]<0)
                rc.NetDemand[i]=0;
        }
    }

    // 计算计划产出量,通过
    public static void Process_PlannedOutput(){
        rc.PlannedReceipt[0] = 0;
        for (int i = 1; i<11; i++){
            rc.PlannedOutput[i]=rc.PlannedReceipt[i];
        }
    }

    // 计算计划投入量，通过
    public static void Process_PlannedInput(){
        for (int i = 1; i<11; i++){
            if((i-tc.LeadTime)>=0)
                rc.PlannedInput[i-tc.LeadTime]=rc.PlannedOutput[i];
        }
    }

    // 计算可供销售量:计划产出-（本周的合同量+后一周的合同量），通过
    public static void Process_AvailableSales(){
        rc.AvailableSales[0]=rc.EstimatedInventory[0];
        for (int i = 1; i<11; i++){
            int result = rc.PlannedOutput[i]-(rc.ContractVol[i]+rc.ContractVol[i+1]);
            if (result>0)
                rc.AvailableSales[i]=result;
            else
                rc.AvailableSales[i]=0;
        }
    }

    public static void Process_Table(){
        // 预测量（11个值）、合同量（11个值）原样输出,通过
        rc.ForecastVol=tc.ForecastVol;
        rc.ContractVol=tc.ContractVol;
    }

    public static void OutputTable(){
        System.out.print("时段,");
        for(int i=0; i<11; i++){
            int week=i+1;
            System.out.print("第"+week+"周,");
        }
        System.out.println("");
        System.out.print("预测量,");
        for(int i=0; i<11; i++){
            System.out.print(rc.ForecastVol[i]+",");
        }
        System.out.println("");
        System.out.print("合同量,");
        for(int i=0; i<11; i++){
            System.out.print(rc.ContractVol[i]+",");
        }
        System.out.println("");
        System.out.print("毛需求,");
        for(int i=0; i<11; i++){
            System.out.print(rc.GrossDemand[i]+",");
        }
        System.out.println("");
        System.out.print("计划接收量,");
        for(int i=0; i<11; i++){
            System.out.print(rc.PlannedReceipt[i]+",");
        }
        System.out.println("");
        System.out.print("预计库存量,");
        for(int i=0; i<11; i++){
            System.out.print(rc.EstimatedInventory[i]+",");
        }
        System.out.println("");
        System.out.print("净需求,");
        for(int i=0; i<11; i++){
            System.out.print(rc.NetDemand[i]+",");
        }
        System.out.println("");
        System.out.print("计划产出量,");
        for(int i=0; i<11; i++){
            System.out.print(rc.PlannedOutput[i]+",");
        }
        System.out.println("");
        System.out.print("计划投入量,");
        for(int i=0; i<11; i++){
            System.out.print(rc.PlannedInput[i]+",");
        }
        System.out.println("");
        System.out.print("可供销售量,");
        for(int i=0; i<11; i++){
            System.out.print(rc.AvailableSales[i]+",");
        }
    }


    public static void PrintInfoForTest() throws ClassNotFoundException, IllegalAccessException {
        //使用反射技术完成对象属性的输出
        Class<?> c = null;
        c = Class.forName("cn.classfile.tableClass");
        Field[] fields = c.getDeclaredFields();

        for(Field f:fields){
            f.setAccessible(true);
        }
        //输出p1的所有属性
        System.out.println("=============About tc===============");
        for(Field f:fields){
            String field = f.toString().substring(f.toString().lastIndexOf(".")+1);         //取出属性名称
            System.out.println("p1."+field+" --> "+f.get(tc));
        }
    }

    public static void main(String[] args) throws IOException, IllegalAccessException, ClassNotFoundException {
        ReadTable();
        //PrintInfoForTest();//输出测试方法
        Process_Table();
        Process_GrossDemand();
        Process_PlannedReceipt_And_EstimatedInventory();
        Process_NetDemand();
        Process_PlannedOutput();
        Process_PlannedInput();
        Process_AvailableSales();
        OutputTable();
    }
}
