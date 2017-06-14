package cn.classfile;

/**
 * Created by ZNing on 2017/6/13.
 */
public class tableClass {
    public int MaterId; //物料号
    public String MaterName; // 物料名称
    public String PlannedTime; // 计划时间
    public String PlannedPeer; // 计划人
    public int SafetyStock; // 安全库存量
    public int ExistingStock; // 现有库存量
    public int LeadTime; // 提前期
    public int BatchVol; // 批量
    public int BatchIncrement; // 批量增量
    public int NeededTimeEdge; // 需求时界
    public int PlannedTimeEdge; // 计划时界
    public int[] ForecastVol = new int[13]; // 预测量（11个值）
    public int[] ContractVol = new int[13]; // 合同量（11个值）
}
