<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${formTitle} — Library Management</title>
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
        <h1>📗 ${formTitle}</h1>
        <a href="${pageContext.request.contextPath}/books" class="btn btn-secondary">← Back to Books</a>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">❌ ${errorMessage}</div>
    </c:if>

    <div class="card">
        <div class="card-header">${formTitle}</div>
        <div class="card-body">

            <c:choose>
                <%-- ========== ADD FORM (no id yet) ========== --%>
                <c:when test="${book.id == null}">
                    <form:form action="${pageContext.request.contextPath}/books/save"
                               method="post" modelAttribute="book">

                        <div class="form-row">
                            <div class="form-group">
                                <label>Title <span class="req">*</span></label>
                                <form:input path="title" cssClass="form-control"
                                            placeholder="e.g. To Kill a Mockingbird"/>
                                <form:errors path="title" cssClass="invalid-feedback"/>
                            </div>
                            <div class="form-group">
                                <label>ISBN <span class="req">*</span></label>
                                <form:input path="isbn" cssClass="form-control"
                                            placeholder="e.g. 978-0-06-112008-4"/>
                                <form:errors path="isbn" cssClass="invalid-feedback"/>
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label>Genre <span class="req">*</span></label>
                                <form:select path="genre" cssClass="form-control">
                                    <form:option value="" label="-- Select Genre --"/>
                                    <form:option value="Literary Fiction"/>
                                    <form:option value="Fantasy"/>
                                    <form:option value="Science Fiction"/>
                                    <form:option value="Dystopian Fiction"/>
                                    <form:option value="Historical Fiction"/>
                                    <form:option value="Magical Realism"/>
                                    <form:option value="Mystery"/>
                                    <form:option value="Thriller"/>
                                    <form:option value="Romance"/>
                                    <form:option value="Non-Fiction"/>
                                    <form:option value="Psychological Fiction"/>
                                    <form:option value="Biography"/>
                                    <form:option value="Political Satire"/>
                                </form:select>
                                <form:errors path="genre" cssClass="invalid-feedback"/>
                            </div>
                            <div class="form-group">
                                <label>Publication Year <span class="req">*</span></label>
                                <form:input path="publicationYear" type="number"
                                            cssClass="form-control"
                                            placeholder="e.g. 2023" min="1000" max="2026"/>
                                <form:errors path="publicationYear" cssClass="invalid-feedback"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>Author <span class="req">*</span></label>
                            <select name="authorId" class="form-control" required>
                                <option value="">-- Select Author --</option>
                                <c:forEach var="author" items="${authors}">
                                    <option value="${author.id}"
                                        <c:if test="${author.id == selectedAuthorId}">selected</c:if>>
                                        ${author.name} (${author.nationality})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn btn-success">💾 Save Book</button>
                            <a href="${pageContext.request.contextPath}/books" class="btn btn-secondary">Cancel</a>
                        </div>
                    </form:form>
                </c:when>

                <%-- ========== EDIT FORM (has id) ========== --%>
                <c:otherwise>
                    <form:form action="${pageContext.request.contextPath}/books/update/${book.id}"
                               method="post" modelAttribute="book">

                        <div class="form-row">
                            <div class="form-group">
                                <label>Title <span class="req">*</span></label>
                                <form:input path="title" cssClass="form-control"/>
                                <form:errors path="title" cssClass="invalid-feedback"/>
                            </div>
                            <div class="form-group">
                                <label>ISBN <span class="req">*</span></label>
                                <form:input path="isbn" cssClass="form-control"/>
                                <form:errors path="isbn" cssClass="invalid-feedback"/>
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label>Genre <span class="req">*</span></label>
                                <form:select path="genre" cssClass="form-control">
                                    <form:option value="" label="-- Select Genre --"/>
                                    <form:option value="Literary Fiction"/>
                                    <form:option value="Fantasy"/>
                                    <form:option value="Science Fiction"/>
                                    <form:option value="Dystopian Fiction"/>
                                    <form:option value="Historical Fiction"/>
                                    <form:option value="Magical Realism"/>
                                    <form:option value="Mystery"/>
                                    <form:option value="Thriller"/>
                                    <form:option value="Romance"/>
                                    <form:option value="Non-Fiction"/>
                                    <form:option value="Psychological Fiction"/>
                                    <form:option value="Biography"/>
                                    <form:option value="Political Satire"/>
                                </form:select>
                                <form:errors path="genre" cssClass="invalid-feedback"/>
                            </div>
                            <div class="form-group">
                                <label>Publication Year <span class="req">*</span></label>
                                <form:input path="publicationYear" type="number"
                                            cssClass="form-control" min="1000" max="2026"/>
                                <form:errors path="publicationYear" cssClass="invalid-feedback"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>Author <span class="req">*</span></label>
                            <select name="authorId" class="form-control" required>
                                <c:forEach var="author" items="${authors}">
                                    <option value="${author.id}"
                                        <c:if test="${book.author != null && book.author.id == author.id}">selected</c:if>
                                        <c:if test="${author.id == selectedAuthorId}">selected</c:if>>
                                        ${author.name} (${author.nationality})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn btn-warning">✏️ Update Book</button>
                            <a href="${pageContext.request.contextPath}/books" class="btn btn-secondary">Cancel</a>
                        </div>
                    </form:form>
                </c:otherwise>
            </c:choose>

        </div>
    </div>
</div>
</body>
</html>
