<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${author.name} — Library Management</title>
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
        <h1>✍️ ${author.name}</h1>
        <div style="display:flex;gap:0.6rem;">
            <a href="${pageContext.request.contextPath}/authors/edit/${author.id}"
               class="btn btn-warning">✏️ Edit Author</a>
            <a href="${pageContext.request.contextPath}/authors"
               class="btn btn-secondary">← Back</a>
        </div>
    </div>

    <%-- Author detail card --%>
    <div class="card">
        <div class="card-header">Author Details</div>
        <div class="card-body">
            <table class="detail-table">
                <tr>
                    <td>Full Name</td>
                    <td><strong>${author.name}</strong></td>
                </tr>
                <tr>
                    <td>Nationality</td>
                    <td><span class="badge badge-nation">${author.nationality}</span></td>
                </tr>
                <tr>
                    <td>Biography</td>
                    <td style="line-height:1.7;">${author.biography}</td>
                </tr>
                <tr>
                    <td>Total Books</td>
                    <td><span class="badge badge-count">${author.books.size()} book(s)</span></td>
                </tr>
            </table>
        </div>
    </div>

    <%-- Books by this author --%>
    <div class="card">
        <div class="card-header">
            Books by ${author.name}
            <span style="font-weight:400;font-size:0.88rem;">&nbsp;(${author.books.size()})</span>
        </div>
        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Title</th>
                        <th>ISBN</th>
                        <th>Genre</th>
                        <th>Year</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="book" items="${author.books}" varStatus="s">
                        <tr>
                            <td>${s.index + 1}</td>
                            <td><strong>${book.title}</strong></td>
                            <td><code style="font-size:0.8rem;">${book.isbn}</code></td>
                            <td><span class="badge badge-genre">${book.genre}</span></td>
                            <td><span class="badge badge-year">${book.publicationYear}</span></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/books/edit/${book.id}"
                                   class="btn btn-warning">✏️ Edit</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty author.books}">
                        <tr>
                            <td colspan="6" style="text-align:center;color:#999;padding:1.5rem;">
                                No books recorded for this author.
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
