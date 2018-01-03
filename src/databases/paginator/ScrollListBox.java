package databases.paginator;

import server.comm.DataMap;

import java.util.List;

public class ScrollListBox {
    private Integer totalRow;
    private List<DataMap> list;

    public ScrollListBox(int total, List<DataMap> list) {
        this.totalRow = total;
        this.list = list;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public List<DataMap> getList() {
        return list;
    }

    public void setList(List<DataMap> list) {
        this.list = list;
    }
}
