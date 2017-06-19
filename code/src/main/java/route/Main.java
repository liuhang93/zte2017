package route;

import util.FileUtil;
import util.TimeUtil;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("please input args: graphFilePath, conditionFilePath, resultFilePath");
            return;
        }

        String graphFilePath = args[0];
        String conditionFilePath = args[1];
        String resultFilePath = args[2];

        TimeUtil.printLog("Begin");

        // 读取输入文件
        String[] graphContent = FileUtil.read(graphFilePath, null);
        String[] conditionContent = FileUtil.read(conditionFilePath, null);

        // 功能实现入口
        String[] resultContents = Route.searchRoute(graphContent, conditionContent);

        // 写入输出文件
        if (hasResults(resultContents)) {
            FileUtil.write(resultFilePath, resultContents, false);
        } else {
            FileUtil.write(resultFilePath, new String[]{"NA"}, false);
        }
        TimeUtil.printLog("End");
        System.out.println("程序运行完毕，运行结果存储在:"+resultFilePath+"; 总运行时长为" + TimeUtil.getUsedTime() + " ms");
    }

    private static boolean hasResults(String[] resultContents) {
        if (resultContents == null) {
            return false;
        }
        for (String contents : resultContents) {
            if (contents != null && !contents.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
