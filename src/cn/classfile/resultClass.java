package cn.classfile;

/**
 * Created by ZNing on 2017/6/13.
 */
public class resultClass {
    public int[] ForecastVol = new int[13]; // 预测量（11个值）
    public int[] ContractVol = new int[13]; // 合同量（11个值）
    public int[] GrossDemand = new int[13];//毛需求
    public int[] PlannedReceipt = new int[13];// 计划接收量
    public int[] EstimatedInventory = new int[13];// 预计库存量
    public int[] NetDemand = new int[13];// 净需求
    public int[] PlannedOutput = new int[13];// 计划产出量
    public int[] PlannedInput = new int[13];// 计划投入量
    public int[] AvailableSales = new int[13];// 可供销售量
}
