package com.theater.utils;

import com.theater.model.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:theater.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String createUsers = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    level TEXT NOT NULL,
                    stardust INTEGER NOT NULL,
                    consumption INTEGER NOT NULL,
                    created_date TEXT NOT NULL,
                    status TEXT NOT NULL
                );
                """;

        String createAdmins = """
                CREATE TABLE IF NOT EXISTS admins (
                    admin_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL
                );
                """;

        String createCinema = """
                CREATE TABLE IF NOT EXISTS cinema (
                    cinema_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cinema_name TEXT NOT NULL,
                    info TEXT
                );
                """;

        String createAuditoriums = """
                CREATE TABLE IF NOT EXISTS auditoriums (
                    auditorium_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cinema_id INTEGER NOT NULL,
                    auditorium_name TEXT NOT NULL,
                    seat_amount INTEGER NOT NULL,
                    auditorium_type TEXT NOT NULL,
                    seat_codes TEXT NOT NULL,
                    FOREIGN KEY (cinema_id) REFERENCES cinema(cinema_id)
                );
                """;

        String createMovies = """
                CREATE TABLE IF NOT EXISTS movies (
                    movie_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    movie_name TEXT NOT NULL,
                    info TEXT,
                    publish_date TEXT NOT NULL,
                    take_down_date TEXT NOT NULL,
                    status TEXT NOT NULL
                );
                """;

        String createSessions = """
                CREATE TABLE IF NOT EXISTS sessions (
                    session_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    movie_id INTEGER NOT NULL,
                    cinema_id INTEGER NOT NULL,
                    auditorium_id INTEGER NOT NULL,
                    session_time TEXT NOT NULL,
                    status TEXT NOT NULL,
                    seat_sold INTEGER NOT NULL,
                    FOREIGN KEY (movie_id) REFERENCES movies(movie_id),
                    FOREIGN KEY (cinema_id) REFERENCES cinema(cinema_id),
                    FOREIGN KEY (auditorium_id) REFERENCES auditoriums(auditorium_id)
                );
                """;

        String createOrders = """
                CREATE TABLE IF NOT EXISTS orders (
                    order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    cinema_id INTEGER NOT NULL,
                    movie_id INTEGER NOT NULL,
                    session_id INTEGER NOT NULL,
                    seat TEXT NOT NULL,
                    order_time TEXT NOT NULL,
                    type TEXT NOT NULL,
                    price INTEGER NOT NULL,
                    status TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(user_id),
                    FOREIGN KEY (cinema_id) REFERENCES cinema(cinema_id),
                    FOREIGN KEY (movie_id) REFERENCES movies(movie_id),
                    FOREIGN KEY (session_id) REFERENCES sessions(session_id)
                );
                """;

        String createSeats = """
                CREATE TABLE IF NOT EXISTS seats (
                    seat_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    session_id INTEGER NOT NULL,
                    seat_code TEXT NOT NULL,
                    priority INTEGER NOT NULL,
                    status TEXT NOT NULL,
                    order_id INTEGER,
                    FOREIGN KEY (session_id) REFERENCES sessions(session_id),
                    FOREIGN KEY (order_id) REFERENCES orders(order_id)
                );
                """;

        String createProducts = """
                CREATE TABLE IF NOT EXISTS products (
                    product_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    product_name TEXT NOT NULL,
                    movie_id INTEGER NOT NULL,
                    info TEXT,
                    price INTEGER NOT NULL,
                    publish_time TEXT NOT NULL,
                    amount INTEGER NOT NULL,
                    status TEXT NOT NULL,
                    FOREIGN KEY (movie_id) REFERENCES movies(movie_id)
                );
                """;

        String createPurchases = """
                CREATE TABLE IF NOT EXISTS purchases (
                    purchase_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    product_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    order_time TEXT NOT NULL,
                    type TEXT NOT NULL,
                    sold_price INTEGER NOT NULL,
                    FOREIGN KEY (product_id) REFERENCES products(product_id),
                    FOREIGN KEY (user_id) REFERENCES users(user_id)
                );
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsers);
            stmt.execute(createAdmins);
            stmt.execute(createCinema);
            stmt.execute(createAuditoriums);
            stmt.execute(createMovies);
            stmt.execute(createSessions);
            stmt.execute(createOrders);
            stmt.execute(createSeats);
            stmt.execute(createProducts);
            stmt.execute(createPurchases);
            System.out.println("Theater database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }

        seedDefaultData();
    }

    private static void seedDefaultData() {
        AdminDAO adminDAO = new AdminDAOImpl();
        if (adminDAO.login("admin", "admin123") == -1) {
            adminDAO.addAdmin(new Admin("admin", "admin123"));
        }

        CinemaDAO cinemaDAO = new CinemaDAOImpl();
        if (cinemaDAO.listCinema().isEmpty()) {
            Cinema cinema = new Cinema("東星影城 信義店", "台北市信義區，每日 10:00-24:00");
            cinemaDAO.insertCinema(cinema);
            cinema = new Cinema("東星影城 板橋店", "新北市板橋區，每日 10:00-23:00");
            cinemaDAO.insertCinema(cinema);
        }

        MovieDAO movieDAO = new MovieDAOImpl();
        if (movieDAO.listMovie(null).isEmpty()) {
            Movie movie = new Movie("星際遠征", "科幻冒險大片",
                    LocalDate.now().minusDays(7), LocalDate.now().plusMonths(2));
            movie.setStatus(Movie.MovieStatus.IN_THEATER);
            movieDAO.insertMovie(movie);

            movie = new Movie("夏日戀曲", "浪漫愛情片",
                    LocalDate.now().plusDays(3), LocalDate.now().plusMonths(1));
            movie.setStatus(Movie.MovieStatus.UPCOMING);
            movieDAO.insertMovie(movie);
        }

        CinemaDAOImpl cinemaDAOImpl = new CinemaDAOImpl();
        List<String> cinemaNames = cinemaDAO.listCinema();
        if (!cinemaNames.isEmpty()) {
            Cinema firstCinema = cinemaDAOImpl.findCinemaByName(cinemaNames.getFirst());
            AuditoriumDAO auditoriumDAO = new AuditoriumDAOImpl();
            if (firstCinema != null
                    && auditoriumDAO.listAuditoriumsByCinema(firstCinema.getCinemaID()).isEmpty()) {
                String[] seats = generateSeatCodes(8, 10);
                Auditorium auditorium = new Auditorium("1 號廳",
                        Auditorium.getTotalSeatsCount(Auditorium.AuditoriumType.NORMAL_L),
                        Auditorium.AuditoriumType.NORMAL_L, seats);
                auditoriumDAO.insertAuditorium(auditorium, firstCinema.getCinemaID());
            }

            SessionDAO sessionDAO = new SessionDAOImpl();
            MovieDAOImpl movieDAOImpl = new MovieDAOImpl();
            List<String> movies = movieDAO.listMovie(Movie.MovieStatus.IN_THEATER);
            if (!movies.isEmpty() && firstCinema != null) {
                Movie movie = movieDAOImpl.findMovieByName(movies.getFirst());
                List<Auditorium> auditoriums =
                        auditoriumDAO.listAuditoriumsByCinema(firstCinema.getCinemaID());
                if (movie != null && !auditoriums.isEmpty()
                        && sessionDAO.findSession(movie.getMovieID(), false).isEmpty()) {
                    Auditorium auditorium = auditoriums.getFirst();
                    Session session = new Session(movie.getMovieID(), firstCinema.getCinemaID(),
                            auditorium.getAuditoriumID(),
                            LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(19, 0)));
                    session.setStatus(Session.SessionStatus.Selling);
                    if (sessionDAO.insertSession(session)) {
                        new SeatDAOImpl().initSessionSeats(session.getSessionID(), 8, 10);
                    }
                }
            }

            List<String> activeMovies = movieDAO.listMovie(Movie.MovieStatus.IN_THEATER);

            if (!activeMovies.isEmpty()) {
                // 抓取目前上映中的第一部電影（例如：星際遠征），用來綁定周邊食品的外鍵
                Movie currentMovie = movieDAOImpl.findMovieByName(activeMovies.getFirst());

                if (currentMovie != null) {
                    ProductDAO productDAO = new ProductDAOImpl();
                    // 檢查目前這部電影是否已經建立了周邊商品，如果是空的才進行預熱防重灌
                    if (productDAO.listProduct(currentMovie.getMovieID()).isEmpty()) {

                        // 1. 創立吉拿棒 (售價: 90, 庫存: 100)
                        Product churros = new Product(
                                "現烤肉桂吉拿棒",
                                currentMovie.getMovieID(),
                                "外酥內軟、香甜肉桂風味，影城看片絕配！",
                                90,
                                LocalDate.now(),
                                100,
                                Product.ProductStatus.SELLING
                        );
                        productDAO.insertProduct(churros);

                        // 2. 創立爆米花 (售價: 130, 庫存: 150)
                        Product popcorn = new Product(
                                "招牌雙色爆米花 (大)",
                                currentMovie.getMovieID(),
                                "經典爆米花，甜鹹混合黃金比例。",
                                130,
                                LocalDate.now(),
                                150,
                                Product.ProductStatus.SELLING
                        );
                        productDAO.insertProduct(popcorn);

                        // 3. 創立飲料 (售價: 50, 庫存: 200)
                        Product drink = new Product(
                                "冰涼可口可樂 (大)",
                                currentMovie.getMovieID(),
                                "暢快氣泡感，暢飲無限。",
                                50,
                                LocalDate.now(),
                                200,
                                Product.ProductStatus.SELLING
                        );
                        productDAO.insertProduct(drink);

                        System.out.println("電影美味點心（吉拿棒、爆米花、飲料）已成功匯入商品資料庫！");
                    }
                }
                }
        }
    }

    static String[] generateSeatCodes(int rows, int cols) {
        String[] codes = new String[rows * cols];
        int index = 0;
        for (int r = 1; r <= rows; r++) {
            char rowLetter = (char) ('A' + (r - 1));
            for (int c = 1; c <= cols; c++) {
                codes[index++] = String.format("%c%02d", rowLetter, c);
            }
        }
        return codes;
    }
}
