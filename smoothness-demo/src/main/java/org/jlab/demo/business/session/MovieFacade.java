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

    public MovieFacade() {
        super(Movie.class);
    }

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

    public void addMovie(String title, String description, String rating, Integer duration, Date release) throws UserFriendlyException {
        Movie movie = new Movie();

        updateMovie(movie, title, description, rating, duration, release);

        em.persist(movie);
    }

    public void removeMovie(BigInteger[] idArray) throws UserFriendlyException {
        if(idArray == null || idArray.length == 0) {
            throw new UserFriendlyException("Please select at least one movie to remove");
        }

        for(BigInteger id: idArray) {
            Movie movie = find(id);

            em.remove(movie);
        }
    }

    public void editMovie(BigInteger id, String title, String description, String rating, Integer duration, Date release) throws UserFriendlyException {
        Movie movie = find(id);

        updateMovie(movie, title, description, rating, duration, release);
    }

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
