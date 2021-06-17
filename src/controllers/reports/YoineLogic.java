package controllers.reports;

import models.Report;

public class YoineLogic {

    public void yoinePlus(Report r) {
        int count = r.getYoineCount();
        count++;
        r.setYoineCount(count);
    }

}
