import excel.ExcelParseVo;
import excel.ExcelUtil;
import org.apache.commons.collections4.CollectionUtils;
import util.JarPathUtil;
import vo.JieShunVo;
import vo.YinlianVo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParkingFeeAnalysis {
    private JPanel parkingFeeAnalysis;
    private JTextArea textArea;
    private JButton button;

    // 得到显示器屏幕的宽高
    public static int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    // 定义窗体的宽高
    public static int windowsWedth = 600;
    public static int windowsHeight = 600;

    public ParkingFeeAnalysis() {
        // 设置按钮尺寸
        Dimension preferredSize = new Dimension(0,50);
        button.setPreferredSize(preferredSize);
        // textArea不可编辑
        textArea.setEditable(false);
        // textArea添加滚动条
        parkingFeeAnalysis.add(new JScrollPane(textArea));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    textArea.append("==================开始分析==================  " + sdf.format(new Date()) + "\r\n");
                    ExcelParseVo<JieShunVo> jieShunVoExcelParseVo = parseJieShunData(JarPathUtil.getJieShunFolderPath());
                    if (CollectionUtils.isNotEmpty(jieShunVoExcelParseVo.getMessages())) {
                        showErrorMsgTip(jieShunVoExcelParseVo.getMessages());
                        return;
                    }
                    ExcelParseVo<YinlianVo> yinlianVoExcelParseVo = parseYinlianData(JarPathUtil.getYinLianFolderPath());
                    if (CollectionUtils.isNotEmpty(yinlianVoExcelParseVo.getMessages())) {
                        showErrorMsgTip(yinlianVoExcelParseVo.getMessages());
                        return;
                    }
                    compareAndOutput(jieShunVoExcelParseVo.getParseRs(), yinlianVoExcelParseVo.getParseRs());
                    textArea.append("==================分析完成==================  " + sdf.format(new Date()) + "\r\n");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    textArea.append(getStackTrace(ex) +"\r\n");
                    showSystemExitTip();
                }
            }
        });
    }

    private void showSystemExitTip() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        textArea.append("==================发生异常已退出分析==================  " + sdf.format(new Date()) + "\r\n");
    }

    private void showErrorMsgTip(List<String> msgs) {
        for (String msg : msgs) {
            textArea.append(msg +"\r\n");
        }
        showSystemExitTip();
    }

    /**
     * 打印堆栈信息
     * @param e
     * @return
     */
    public static String getStackTrace(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();

        } catch (Exception e2) {
            e2.printStackTrace();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }

    /**
     *
     * @param jieShunVos
     * @param yinlianVos
     */
    private void compareAndOutput(List<JieShunVo> jieShunVos, List<YinlianVo> yinlianVos) throws Exception {
        Map<String, List<YinlianVo>> carNum2YinlianMap = yinlianVos.stream().collect(Collectors.groupingBy(YinlianVo::getCarNum));
        List<JieShunVo> parseRs = new ArrayList<>();
        jieShunVos.forEach(x ->{
            if (!matchInYinlian(x, carNum2YinlianMap)) {
                parseRs.add(x);
            }
        });
        outPutRs(parseRs);
    }

    private void outPutRs(List<JieShunVo> parseRs) throws Exception {
        // 取捷顺文件日期
        if (CollectionUtils.isNotEmpty(parseRs)) {
            SimpleDateFormat collectedDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(collectedDateStr.parse(parseRs.get(0).getCollectedTime()));
            String pathName = JarPathUtil.getRootPath() + "\\" + dateStr + "对比结果.xlsx";
            ExcelUtil.writeExcel(pathName, parseRs);
            textArea.append("生成文件：" + pathName + "\r\n");
        } else {
            textArea.append("无差异结果产生" + "\r\n");
        }
    }

    /**
     * 捷顺数据是否在银联数据中
     * @param jieShunVo
     * @param carNum2YinlianMap
     * @return
     */
    private boolean matchInYinlian(JieShunVo jieShunVo, Map<String, List<YinlianVo>> carNum2YinlianMap) {
        if (null == carNum2YinlianMap.get(jieShunVo.getCarNum())) {
            return false;
        }

        List<YinlianVo> yinlianVos = carNum2YinlianMap.get(jieShunVo.getCarNum());
        for (YinlianVo yinlianVo : yinlianVos) {
            // 捷顺付款时间与银联出场时间比较
            if (jieShunVo.getCollectedTime().equals(yinlianVo.getCarOutTime())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 捷顺数据解析
     * @param path
     * @return
     */
    private ExcelParseVo<JieShunVo> parseJieShunData(String path) throws Exception {
        ExcelParseVo excelParseVo = new ExcelParseVo();
        File file = new File(path);
        if (!file.exists()) {
            excelParseVo.addMessages("can not found folder： " + path);
            return excelParseVo;
        }
        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xls") || name.endsWith(".xlsx");
            }
        });
        if (files.length == 0) {
            excelParseVo.addMessages("can not found any excel file in this folder： " + path);
            return excelParseVo;
        }
        return ExcelUtil.readJieShunExcel(files[0].getAbsolutePath());
    }

    /**
     * 解析银联数据
     * @param path
     * @return
     */
    private ExcelParseVo<YinlianVo> parseYinlianData(String path) throws Exception {
        ExcelParseVo excelParseVo = new ExcelParseVo();
        File file = new File(path);
        if (!file.exists()) {
            excelParseVo.addMessages("can not found folder： " + path);
            return excelParseVo;
        }
        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xls") || name.endsWith(".xlsx");
            }
        });
        if (files.length == 0) {
            excelParseVo.addMessages("can not found any excel file in this folder： " + path);
            return excelParseVo;
        }
        return ExcelUtil.readYinlianExcel(files[0].getAbsolutePath());
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("曾导的停车费差异分析工具");
        frame.setContentPane(new ParkingFeeAnalysis().parkingFeeAnalysis);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // 设置窗体位置和大小,居中显示
        frame.setBounds((width - windowsWedth) / 2,
                (height - windowsHeight) / 2, windowsWedth, windowsHeight);

        frame.setVisible(true);
    }

}
