<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Authors — Library Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<nav class="navbar">
    <a href="${pageContext.request.contextPath}/" class="navbar-brand">
        <span class="icon">📚</span>
        <span class="brand-text">LibraryMS</span>
    </a>
    <div class="navbar-links">
        <a href="${pageContext.request.contextPath}/">Home</a>
        <a href="${pageContext.request.contextPath}/books">Books</a>
        <a href="${pageContext.request.contextPath}/authors" class="active">Authors</a>
    </div>
</nav>

<div class="container">

    <div class="page-header">
        <h1>✍️ Authors</h1>
        <a href="${pageContext.request.contextPath}/authors/new" class="btn btn-primary">+ Add New Author</a>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">✅ ${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">❌ ${errorMessage}</div>
    </c:if>

    <div class="card">
        <div class="card-header">All Authors &nbsp;
            <span style="font-weight:400;font-size:0.88rem;">(${authors.size()} records)</span>
        </div>
        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Name</th>
                        <th>Nationality</th>
                        <th>Biography</th>
                        <th>Books</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="author" items="${authors}" varStatus="s">
                        <tr>
                            <td>${s.index + 1}</td>
                            <td><strong>${author.name}</strong></td>
                            <td><span class="badge badge-nation">${author.nationality}</span></td>
                            <td style="max-width:280px;font-size:0.85rem;color:#555;line-height:1.4;">
                                <c:choose>
                                    <c:when test="${author.biography != null && author.biography.length() > 90}">
                                        ${author.biography.substring(0, 90)}…
                                    </c:when>
                                    <c:otherwise>${author.biography}</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <span class="badge badge-count">${author.books.size()} book(s)</span>
                            </td>
                            <td style="display:flex;gap:0.4rem;flex-wrap:wrap;">
                                <a href="${pageContext.request.contextPath}/authors/${author.id}"
                                   class="btn btn-primary">👁 View</a>
                                <a href="${pageContext.request.contextPath}/authors/edit/${author.id}"
                                   class="btn btn-warning">✏️ Edit</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty authors}">
                        <tr>
                            <td colspan="6" style="text-align:center;color:#999;padding:2.5rem;">
                                No authors found.
                                <a href="${pageContext.request.contextPath}/authors/new">Add the first one!</a>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

</div>
</body>
</html>
