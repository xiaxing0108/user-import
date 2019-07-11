package constant;

import java.util.*;

public class ExportConstant {

    /**
     * 去哪儿总表表统计站点
     */
    public static final Map<String,String> airPortMap = new LinkedHashMap<>();

    /**
     * 同程总表统计站点
     */
    public static final Map<String,String> airPortMapTC = new LinkedHashMap<>();

    /**
     * 携程PST统计站点
     */
    public static final Map<String,String> xcPortPst = new LinkedHashMap<>();

    /**
     * 同程PST统计站点
     */
    public static final Map<String,String> tcPortPst = new LinkedHashMap<>();
    static {
        airPortMap.put("SHA","虹桥");
        airPortMap.put("PVG","浦东");
        airPortMap.put("CAN","广州");
        airPortMap.put("SZX","深圳");
        airPortMap.put("KMG","昆明");
        airPortMap.put("XIY","西安");

        airPortMapTC.put("SHA","虹桥");
        airPortMapTC.put("CKG","重庆");
        airPortMapTC.put("PVG","浦东");
        airPortMapTC.put("XIY","西安");
        airPortMapTC.put("SZX","深圳");
        airPortMapTC.put("KMG","昆明");
        airPortMapTC.put("PEK","北京");

        tcPortPst.put("SHA","虹桥");
        tcPortPst.put("CKG","重庆");
        tcPortPst.put("PVG","浦东");
        tcPortPst.put("XIY","西安");
        tcPortPst.put("KMG","昆明");
        tcPortPst.put("PEK","北京");

        xcPortPst.put("XIY","西安");
        xcPortPst.put("CKG","重庆");
        xcPortPst.put("PVG","浦东");
        xcPortPst.put("SHA","虹桥");
        xcPortPst.put("CAN","广州");
        xcPortPst.put("CTU","成都");
        xcPortPst.put("KMG","昆明");
        xcPortPst.put("PEK","北京");
        xcPortPst.put("SZX","深圳");


    }
}
