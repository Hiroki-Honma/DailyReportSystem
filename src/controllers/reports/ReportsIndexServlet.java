package controllers.reports;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsIndexServlet
 */
@WebServlet("/reports/index")
public class ReportsIndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsIndexServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();


        int page;
        try{
            page = Integer.parseInt(request.getParameter("page"));
        } catch(Exception e) {
            page = 1;
        }

        List<Report> reports = em.createNamedQuery("getAllReports", Report.class)
                .setFirstResult(15 * (page - 1))
                .setMaxResults(15)
                .getResultList();

        long reports_count = (long)em.createNamedQuery("getReportsCount", Long.class)
                   .getSingleResult();

      //リクエストパラメーターの取得
        String yoine = request.getParameter("action");
      //いいねボタンを押されたら
        if(yoine != null){
            Report r = (Report) em.createNamedQuery("getReport", Report.class)
                    .setParameter("id" , Integer.parseInt(request.getParameter("id")))
                    .getSingleResult();

            // YoineLogicでいいねを加算
            YoineLogic yl = new YoineLogic();
            yl.yoinePlus(r);

            //そのレポートのページ数をpageに代入
            int id =r.getId();
            page = (int) (((reports_count - id)/15) + 1);

            em.getTransaction().begin();
            em.persist(r);
            em.getTransaction().commit();

        }



        em.close();

        request.setAttribute("reports", reports);
        request.setAttribute("reports_count", reports_count);
        request.setAttribute("page", page);
        if(request.getSession().getAttribute("flush") != null) {
            request.setAttribute("flush", request.getSession().getAttribute("flush"));
            request.getSession().removeAttribute("flush");
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/index.jsp");
        rd.forward(request, response);
    }

}
