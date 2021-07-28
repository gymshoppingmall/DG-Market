<%@page import="ga.dgmarket.gymshopping.domain.UsedProductExtend"%>
<%@page import="ga.dgmarket.gymshopping.domain.Member"%>
<%@page import="org.eclipse.jdt.internal.compiler.lookup.MemberTypeBinding"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/html;charset=UTF-8"%>

<%
	Member member = (Member)session.getAttribute("member");
	List<UsedProductExtend> usedProductList = (List)request.getAttribute("usedProductList");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>중고거래 메인</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<!-- Popper JS -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js" alt></script>

<style>
	body{
		height: 1000px;
	}
    .jumbotron {
        color: #fff;
        padding:  25px;
    }

    .row{
        padding-left: 250px;
        padding-right: 250px;
    }

    .col-xl-3{
        margin-top: 25px;
        display: none;
    }

    .card-header div{
        margin-right: 0px;
    }

    img{
        width: 100%;
        height: 250px;
        object-fit: cover;
    }

    .card{
        border: black 1px solid;

    }

    hr{
        border: 0px;
        height: 3px;
        background: black;
    }

    input:focus, select:focus ,#search_bt:focus{
        outline: none;
    }

</style>
<script>
$(function(){
    //최초 상품 4개를 보여주기
    loadProduct();
   
    //더 보기 버튼 클릭 시 발생 이벤트
    $("#load").click(function(e){
        e.preventDefault(); //새로고침을 막아주는 함수
        loadProduct(e);
    });
});

//상품 더보기를 선택할 시 숨겨져 있던 상품 보여주기
function loadProduct(e){
    $(".col-xl-3:hidden").slice(0, 4).show();
    if($(".col-xl-3:hidden").length == 0){
        $("#load").css("display", "none");
    }
}

//찜을 추가하는 메서드
function addFavorites(used_product_id, member_id){
	alert("찜 추가하기 상품 번호는 : "+used_product_id+"추가하려는 고객 id : "+member_id);
}

//찜을 삭제하는 메서드
function delFavorites(used_favorites_id){
	alert("찜 삭제하기 상품 번호는 : "+used_favorites_id);
}

</script>
</head>
<body>
	<!-- 중고거래 top_navi -->
	<%@ include file="./inc/top_navi.jsp" %>

    

    <!-- 중고상품의 이미지, 정보 등을 볼 수 있는 하나의 박스 -->
    <!-- 페이징 처리와 비슷하게 한 번에 4개의 상품만을 노출 시키고 +버튼을 눌렀을 때 다음 4개의 상품을 노출 시킬 계획 -->
    <!-- cuur 같은 변수를 만들고 +버튼을 누를 때 마다 1씩 증가 시키고 새로고침을 해도 cuur은 유지되게 만들어야 함 -->


    <div class="row text-center">
        <!--하나의 상품을 나타낼 박스-->
        
        <% for(UsedProductExtend usedProduct : usedProductList){ %>
        <div class="col-xl-3">
            <div class="card">
                <!-- 상품명이 올 곳 -->
                <div class="card-header">
                    <!-- 찜을 했냐 안했냐에 따라서 버튼의 색이 달라지고, 클릭 시 찜의 유무 바뀜 -->
                    <!-- 클릭 시 changeLike() 함수 호출 -->
                    <%if(usedProduct.getFavorites_member()==0){ %>
                    <div class="btn-success" style="font-size: 25px; margin-bottom: 5px;" 
                    onclick="addFavorites(<%=usedProduct.getUsed_product_id()%>, <%=member.getMember_id()%>)">
                    ♥
                    </div>
                    <%} else{ %>
                    <div class="btn-danger" style="font-size: 25px; margin-bottom: 5px;"
                    onclick="delFavorites(<%=usedProduct.getFavorites_id()%>)">
                    ♥
                    </div>
                    <%} %>
                    <strong><%= usedProduct.getUsed_product_name() %></strong>
                </div>
                <!-- 상품 이미지가 올 곳 -->
                <img src="/resources/data/used/product/img/<%= usedProduct.getUsed_img() %>" alt="">
                <div class="card-body">
                    <!-- 상품 가격 올 곳 -->
                    <h5 class="card-title"><%= usedProduct.getUsed_product_price() %></h5>
                    <p class="card-text">
                        <!-- 판매자 아이디 올 곳+링크는 판매자 상점으로 이동 -->
                        판매자 : <a href='/member/used/store?member_id=<%=usedProduct.getMember_id()%>'>[<%=usedProduct.getStorename()%>]</a>
                    </p>
                    <!-- 상품의 상세보기로 이동 -->
                    <a href="/member/used/product/detail?used_product_id=<%=usedProduct.getUsed_product_id()%>" class="btn btn-success">상세보기</a>
                </div>
            </div>            
        </div>   
        <% } %>            
    </div>    
    <div style="width: 100%; height: 100px; padding-top: 35px;" class="text-center">
        <div class="btn btn-success center" id="load" onclick="">더보기</div>
    </div>
</body>
</html>