package org.jlab.demo.business.session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.registry.infomodel.User;

import org.jlab.demo.persistence.entity.Movie;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamValidator;

/**
 * A Movie Service.
 *
 * @author ryans
 */
@Stateless
public class MovieFacade extends AbstractFacade<Movie> {

    @PersistenceContext(unitName = "webappPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Create a new MovieFacade.
     */
    public MovieFacade() {
        super(Movie.class);
    }

    /**
     * Returns all movies in the database.
     *
     * @return A list of all movies
     */
    public List<Movie> filter() {
        int offset = 0;
        int max = Integer.MAX_VALUE;
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Movie> cq = cb.createQuery(Movie.class);
        Root<Movie> root = cq.from(Movie.class);
        cq.select(root);
        List<Predicate> filters = new ArrayList<>();
        // Add filters here
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        List<Order> orders = new ArrayList<>();
        Path p0 = root.get("releaseDate");
        Order o1 = cb.desc(p0);
        orders.add(o1);
        Path p2 = root.get("movieId");
        Order o2 = cb.asc(p2);
        orders.add(o2);
        cq.orderBy(orders);
        return getEntityManager().createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();
    }

    /**
     * Add a new movie.
     *
     * @param title The title
     * @param description The description
     * @param rating The rating
     * @param duration The duration
     * @param release The release date
     * @throws UserFriendlyException If unable to add a new movie
     */
    public void addMovie(String title, String description, String rating, Integer duration, Date release) throws UserFriendlyException {
        Movie movie = new Movie();

        updateMovie(movie, title, description, rating, duration, release);

        em.persist(movie);
    }

    /**
     * Remove a list of movies by ID from the database.
     *
     * @param idArray The array of movie IDs
     * @throws UserFriendlyException If unable to remove the movies
     */
    public void removeMovie(BigInteger[] idArray) throws UserFriendlyException {
        if(idArray == null || idArray.length == 0) {
            throw new UserFriendlyException("Please select at least one movie to remove");
        }

        for(BigInteger id: idArray) {
            Movie movie = find(id);

            em.remove(movie);
        }
    }

    /**
     * Edit a movie with the given ID.
     *
     * @param id The ID of the existing movie to edit
     * @param title The updated title
     * @param description The updated description
     * @param rating The updated rating
     * @param duration The updated duration
     * @param release The updated release date
     * @throws UserFriendlyException If unable to edit the movie
     */
    public void editMovie(BigInteger id, String title, String description, String rating, Integer duration, Date release) throws UserFriendlyException {
        Movie movie = find(id);

        updateMovie(movie, title, description, rating, duration, release);
    }

    /**
     * Edit the provided Movie entity (must already be proxied in JPA).
     *
     * @param movie The Movie
     * @param title The updated title
     * @param description The updated description
     * @param rating The updated rating
     * @param duration The updated duration
     * @param release The updated release date
     * @throws UserFriendlyException If unable to edit the movie
     */
    private void updateMovie(Movie movie, String title, String description, String rating, Integer duration, Date release) throws UserFriendlyException {
        if(title == null || title.isEmpty()) {
            throw new UserFriendlyException("title must not be empty");
        }

        movie.setTitle(title);
        movie.setDescription(description);
        movie.setMpaaRating(rating);
        movie.setDurationMinutes(duration);
        movie.setReleaseDate(release);
    }

    /**
     * Edit just the rating of all movies referenced by IDs.
     *
     * @param idArray The array of movie IDs
     * @param rating The updated rating
     * @throws UserFriendlyException If unable to edit the movies
     */
    public void editMovieRating(BigInteger[] idArray, String rating) throws UserFriendlyException {
        if(idArray == null || idArray.length == 0) {
            throw new UserFriendlyException("Please select at least one movie to remove");
        }

        for(BigInteger id: idArray) {
            Movie movie = find(id);

            movie.setMpaaRating(rating);
        }
    }
}
