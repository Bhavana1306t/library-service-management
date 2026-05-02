<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Library Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<nav class="navbar">
    <a href="${pageContext.request.contextPath}/" class="navbar-brand">
        <span class="icon">📚</span>
        <span class="brand-text">LibraryMS</span>
    </a>
    <div class="navbar-links">
        <a href="${pageContext.request.contextPath}/" class="active">Home</a>
        <a href="${pageContext.request.contextPath}/books">Books</a>
        <a href="${pageContext.request.contextPath}/authors">Authors</a>
    </div>
</nav>

<div class="container">

    <!-- Hero banner -->
    <div class="hero">
        <h1>📚 Library Management System</h1>
        <p>Manage your collection of books and authors — create, browse, and update with ease.</p>
        <div style="display:flex;gap:1rem;justify-content:center;flex-wrap:wrap;">
            <a href="${pageContext.request.contextPath}/books"      class="btn btn-success">📖 Browse Books</a>
            <a href="${pageContext.request.contextPath}/authors"    class="btn btn-primary">✍️ Browse Authors</a>
            <a href="${pageContext.request.contextPath}/books/new"  class="btn btn-warning">+ Add Book</a>
            <a href="${pageContext.request.contextPath}/authors/new"class="btn btn-secondary">+ Add Author</a>
        </div>
    </div>

    <!-- Live stat cards (counts come from HomeController) -->
    <div class="stat-grid">
        <div class="stat-card">
            <div class="stat-icon">📖</div>
            <div class="stat-number">${totalBooks}</div>
            <div class="stat-label">Books in Collection</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">✍️</div>
            <div class="stat-number">${totalAuthors}</div>
            <div class="stat-label">Authors Registered</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">🌍</div>
            <div class="stat-number">7+</div>
            <div class="stat-label">Countries Represented</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">🏆</div>
            <div class="stat-number">3</div>
            <div class="stat-label">Nobel Prize Winners</div>
        </div>
    </div>

    <!-- Quick actions -->
    <div class="card">
        <div class="card-header">🚀 Quick Actions</div>
        <div class="card-body">
            <div class="action-grid">
                <a href="${pageContext.request.contextPath}/books/new"  class="btn btn-primary">📗 Add New Book</a>
                <a href="${pageContext.request.contextPath}/authors/new" class="btn btn-success">🖊️ Add New Author</a>
                <a href="${pageContext.request.contextPath}/books"       class="btn btn-warning">📋 View All Books</a>
                <a href="${pageContext.request.contextPath}/authors"     class="btn btn-secondary">👤 View All Authors</a>
            </div>
        </div>
    </div>

</div>
</body>
</html>
