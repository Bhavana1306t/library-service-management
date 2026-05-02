<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Books — Library Management</title>
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
        <a href="${pageContext.request.contextPath}/books" class="active">Books</a>
        <a href="${pageContext.request.contextPath}/authors">Authors</a>
    </div>
</nav>

<div class="container">

    <div class="page-header">
        <h1>📖 Books Collection</h1>
        <a href="${pageContext.request.contextPath}/books/new" class="btn btn-primary">+ Add New Book</a>
    </div>

    <%-- Flash messages from redirect --%>
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">✅ ${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">❌ ${errorMessage}</div>
    </c:if>

    <%-- Info banner explaining the JOIN --%>
    <div class="alert alert-info">
        ℹ️ This table is populated using a custom <strong>INNER JOIN</strong> JPQL query
        (<code>FROM Book b INNER JOIN b.author a</code>) that fetches book and author
        data in a single query — defined in <code>BookRepository.findAllBooksWithAuthorDetails()</code>.
    </div>

    <div class="card">
        <div class="card-header">All Books &nbsp;
            <span style="font-weight:400;font-size:0.88rem;">(${books.size()} records)</span>
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
                        <th>Author</th>
                        <th>Nationality</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="book" items="${books}" varStatus="s">
                        <tr>
                            <td>${s.index + 1}</td>
                            <td><strong>${book.bookTitle}</strong></td>
                            <td><code style="font-size:0.8rem;">${book.isbn}</code></td>
                            <td><span class="badge badge-genre">${book.genre}</span></td>
                            <td><span class="badge badge-year">${book.publicationYear}</span></td>
                            <td>✍️ ${book.authorName}</td>
                            <td><span class="badge badge-nation">${book.nationality}</span></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/books/edit/${book.bookId}"
                                   class="btn btn-warning">✏️ Edit</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty books}">
                        <tr>
                            <td colspan="8" style="text-align:center;color:#999;padding:2.5rem;">
                                No books found.
                                <a href="${pageContext.request.contextPath}/books/new">Add the first one!</a>
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
