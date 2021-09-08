package excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vo.JieShunVo;
import vo.YinlianVo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelUtil {

    public static final String PAY_METHOD_YINLIAN = "深圳银联";

    /**
     * 从Excel文件读取捷顺数据
     * @param fileName 要读取的Excel文件的全路径文件名称
     * @return 从Excel文件中批量导入数据
     */
    public static ExcelParseVo<JieShunVo> readJieShunExcel(String fileName) throws Exception {
        Workbook workbook = null;
        Sheet sheet = null;
        Row row = null;

        ExcelParseVo<JieShunVo> excelParseVo = new ExcelParseVo<>();

        //读取Excel文件
        File excelFile = new File(fileName.trim());
        InputStream is = new FileInputStream(excelFile);

        //获取Excel工作薄
        if (excelFile.getName().endsWith("xlsx")) {
            workbook = new XSSFWorkbook(is);
        } else {
            workbook = new HSSFWorkbook(is);
        }
        if (workbook == null) {
            excelParseVo.getMessages().add("Excel文件有问题,请检查！");
            return excelParseVo;
        }

        //获取Excel表单
        sheet = workbook.getSheetAt(0);

        List<JieShunVo> jieShunVos = new ArrayList<>();
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            row = sheet.getRow(rowNum);

            // 过滤非深圳银联支付的数据
            String payMethodStr =  getStringValue(row.getCell(5));
            if (!PAY_METHOD_YINLIAN.equals(payMethodStr.trim()))  {
                continue;
            }

            JieShunVo jieShunVo = new JieShunVo();
            String carNum = getJieShunCarNum(getStringValue(row.getCell(1)));
            if (isEmpty(carNum)) {
                excelParseVo.getMessages().add("文件：" + fileName.trim() + "，第【" + (rowNum + 1) + "】行数据获取车牌号码失败");
                continue;
            }
            jieShunVo.setCarNum(carNum);
            jieShunVo.setProjectName(getStringValue(row.getCell(0)));
            jieShunVo.setCarInTime(getStringValue(row.getCell(2)));
            jieShunVo.setCollectedTime(getStringValue(row.getCell(3)));
            jieShunVo.setReceivableMoney(getStringValue(row.getCell(4)));
            jieShunVo.setPayMethod(payMethodStr);
            jieShunVo.setReceiptMoney(getStringValue(row.getCell(6)));
            jieShunVos.add(jieShunVo);
        }
        excelParseVo.setParseRs(jieShunVos);
        is.close();
        return excelParseVo;
    }

    /**
     * 从Excel文件读取银联数据
     * @param fileName 要读取的Excel文件的全路径文件名称
     * @return 从Excel文件中批量导入数据
     */
    public static ExcelParseVo<YinlianVo> readYinlianExcel(String fileName) throws Exception {
        Workbook workbook = null;
        Sheet sheet = null;
        Row row = null;

        ExcelParseVo<YinlianVo> excelParseVo = new ExcelParseVo<>();

        //读取Excel文件
        File excelFile = new File(fileName.trim());
        InputStream is = new FileInputStream(excelFile);

        //获取Excel工作薄
        if (excelFile.getName().endsWith("xlsx")) {
            workbook = new XSSFWorkbook(is);
        } else {
            workbook = new HSSFWorkbook(is);
        }
        if (workbook == null) {
            excelParseVo.getMessages().add("Excel文件有问题,请检查！");
            return excelParseVo;
        }

        //获取Excel表单
        sheet = workbook.getSheetAt(0);

        List<YinlianVo> yinlianVos = new ArrayList<>();
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            row = sheet.getRow(rowNum);
            YinlianVo yinlianVo = new YinlianVo();
            String carNum = getYinlianCarNum(getStringValue(row.getCell(2)));
            if (isEmpty(carNum)) {
                excelParseVo.getMessages().add("文件：" + fileName.trim() + "，第【" + (rowNum + 1) + "】行数据获取车牌号码失败");
                continue;
            }
            yinlianVo.setCarNum(carNum);
            yinlianVo.setFlowNum(getStringValue(row.getCell(0)));
            yinlianVo.setSenselessPFNum(getStringValue(row.getCell(1)));
            yinlianVo.setMoney(getStringValue(row.getCell(3)));
            yinlianVo.setTradeTime(getStringValue(row.getCell(4)));
            yinlianVo.setCarInTime(getStringValue(row.getCell(5)));
            yinlianVo.setCarOutTime(formatDate(getStringValue(row.getCell(6))));
            yinlianVos.add(yinlianVo);
        }
        excelParseVo.setParseRs(yinlianVos);
        is.close();
        return excelParseVo;
    }

    /**
     * 获取捷顺车牌号
     * @param carNum
     * @return
     */
    private static String getJieShunCarNum(String carNum) {
        if (isEmpty(carNum)) {
            return "";
        }
        return carNum.replaceAll("-","").trim();
    }

    /**
     * 获取银联车牌号
     * @param carNum
     * @return
     */
    private static String getYinlianCarNum(String carNum) {
        if (isEmpty(carNum)) {
            return "";
        }
        String[] arr = carNum.split("-");
        if (arr.length < 2) {
            throw new RuntimeException("getYinlianCarNum error, carNum:" + carNum);
        }
        return arr[1].trim();
    }

    /**
     * 银联出场时间格式化为：yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     * @throws ParseException
     */
    private static String formatDate(String date) throws ParseException {
        if (isEmpty(date)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date rs = sdf.parse(date);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs);
    }

    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    private static boolean isEmpty(String str) {
        return null == str || "".equals(str);
    }

    /**
     * 把数据写入到Excel文件
     * @param fileName 自动生成的Excel文件的全路径文件名称
     * @param jieShunVos 要写入到Excel文件中的数据
     */
    public static void writeExcel(String fileName, List<JieShunVo> jieShunVos) throws Exception {
        Workbook workbook = null;
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;

        //创建Excel文件
        File excelFile = new File(fileName.trim());

        //创建Excel工作薄
        if (excelFile.getName().endsWith("xlsx")) {
            workbook = new XSSFWorkbook();
        } else {
            workbook = new HSSFWorkbook();
        }

        //创建Excel表单
        sheet = workbook.createSheet();

        //设置列宽，宽度为256的整数倍
        sheet.setColumnWidth(0, 2816);
        sheet.setColumnWidth(1, 5376);
        sheet.setColumnWidth(2, 5376);
        sheet.setColumnWidth(3, 5376);
        sheet.setColumnWidth(4, 2816);
        sheet.setColumnWidth(5, 5376);

/*
        //设置默认行高(默认为300)
        sheet.setDefaultRowHeight((short) 512);

        //设置合并单元格
        CellRangeAddress titleCellAddresses = new CellRangeAddress(1,2,1,5);
        sheet.addMergedRegion(titleCellAddresses);

        //创建标题行
        row = sheet.createRow(1);
        cell = row.createCell(1, CellType.STRING);
        cell.setCellStyle(getTitleCellStyle(workbook));
        cell.setCellValue("User信息表格");

        //设置合并单元格的边框，这个需要放在创建标题行之后
        setRegionBorderStyle(BorderStyle.THIN, titleCellAddresses, sheet);*/

        //创建表头行
        row = sheet.createRow(0);
        cell = row.createCell(0, CellType.STRING);
        cell.setCellStyle(getHeaderCellStyle(workbook));
        cell.setCellValue("车牌号码");
        cell = row.createCell(1, CellType.STRING);
        cell.setCellStyle(getHeaderCellStyle(workbook));
        cell.setCellValue("入场时间");
        cell = row.createCell(2, CellType.STRING);
        cell.setCellStyle(getHeaderCellStyle(workbook));
        cell.setCellValue("收款时间");
        cell = row.createCell(3, CellType.STRING);
        cell.setCellStyle(getHeaderCellStyle(workbook));
        cell.setCellValue("应收金额(元)");
        cell = row.createCell(4, CellType.STRING);
        cell.setCellStyle(getHeaderCellStyle(workbook));
        cell.setCellValue("付款方式");
        cell = row.createCell(5, CellType.STRING);
        cell.setCellStyle(getHeaderCellStyle(workbook));
        cell.setCellValue("实收金额(元)");

        // 创建表体行
        for(int i = 0; i < jieShunVos.size(); i++) {
            row = sheet.createRow(i + 1);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellStyle(getBodyCellStyle(workbook));
            cell.setCellValue(jieShunVos.get(i).getCarNum());
            cell = row.createCell(1, CellType.STRING);
            cell.setCellStyle(getBodyCellStyle(workbook));
            cell.setCellValue(jieShunVos.get(i).getCarInTime());
            cell = row.createCell(2, CellType.STRING);
            cell.setCellStyle(getBodyCellStyle(workbook));
            cell.setCellValue(jieShunVos.get(i).getCollectedTime());
            cell = row.createCell(3, CellType.STRING);
            cell.setCellStyle(getBodyCellStyle(workbook));
            cell.setCellValue(jieShunVos.get(i).getReceivableMoney());
            cell = row.createCell(4, CellType.STRING);
            cell.setCellStyle(getBodyCellStyle(workbook));
            cell.setCellValue(jieShunVos.get(i).getPayMethod());
            cell = row.createCell(5, CellType.STRING);
            cell.setCellStyle(getBodyCellStyle(workbook));
            cell.setCellValue(jieShunVos.get(i).getReceiptMoney());
        }

        //把Excel工作薄写入到Excel文件
        FileOutputStream os = new FileOutputStream(excelFile);
        workbook.write(os);
        os.flush();
        os.close();
    }

    /**
     * 设置合并单元格的边框
     * @param style 要设置的边框的样式
     * @param cellAddresses 要设置的合并的单元格
     * @param sheet 要设置的合并的单元格所在的表单
     */
    private static void setRegionBorderStyle(BorderStyle style, CellRangeAddress cellAddresses, Sheet sheet) {
        RegionUtil.setBorderTop(style, cellAddresses, sheet);
        RegionUtil.setBorderBottom(style, cellAddresses, sheet);
        RegionUtil.setBorderLeft(style, cellAddresses, sheet);
        RegionUtil.setBorderRight(style, cellAddresses, sheet);
    }

    /**
     * 设置普通单元格的边框
     * @param style 要设置的边框的样式
     * @param cellStyle 单元格样式对象
     */
    private static void setCellBorderStyle(BorderStyle style, CellStyle cellStyle) {
        cellStyle.setBorderTop(style);
        cellStyle.setBorderBottom(style);
        cellStyle.setBorderLeft(style);
        cellStyle.setBorderRight(style);
    }

    /**
     * 设置标题单元格样式
     * @param workbook 工作薄对象
     * @return 单元格样式对象
     */
    private static CellStyle getTitleCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();

        //设置字体
        Font font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 24);
        font.setColor((short) 10);
        cellStyle.setFont(font);

        //设置文字居中显示
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        return cellStyle;
    }


    /**
     * 设置表头单元格样式
     * @param workbook 工作薄对象
     * @return 单元格样式对象
     */
    private static CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();

        //设置字体
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        cellStyle.setFont(font);

        //设置文字居中显示
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //设置单元格的边框
        setCellBorderStyle(BorderStyle.THIN, cellStyle);

        return cellStyle;
    }

    /**
     * 设置表体单元格样式
     * @param workbook 工作薄对象
     * @return 单元格样式对象
     */
    private static CellStyle getBodyCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();

        //设置字体
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);

        //设置文字居中显示
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //设置单元格的边框
        setCellBorderStyle(BorderStyle.THIN, cellStyle);

        return cellStyle;
    }

    /**
     * 获取单元格的值的字符串
     * @param cell 单元格对象
     * @return cell单元格的值的字符串
     */
    private static String getStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                double value = cell.getNumericCellValue();
                return String.valueOf(Math.round(value));
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                cell.setCellType(CellType.STRING);
                return cell.getStringCellValue().trim();
            default:
                return null;
        }
    }

}
