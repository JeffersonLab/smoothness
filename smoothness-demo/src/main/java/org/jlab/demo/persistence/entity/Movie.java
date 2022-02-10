package org.jlab.demo.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author ryans
 */
@Entity
@Table(name = "MOVIE", schema = "SMOOTHNESS_OWNER")
public class Movie implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "MovieId", sequenceName = "MOVIE_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MovieId")
    @Basic(optional = false)
    @NotNull
    @Column(name = "MOVIE_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger movieId;
    @Size(max = 128)
    @Column(length = 128)
    private String title;
    @Column(name = "RELEASE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date releaseDate;
    @Size(max = 512)
    @Column(length = 512)
    private String description;
    @Size(max = 5)
    @Column(name = "MPAA_RATING", length = 5)
    private String mpaaRating;
    @Column(name = "DURATION_MINUTES")
    private Integer durationMinutes;

    /**
     * Create a new Movie.
     */
    public Movie() {

    }

    /**
     * Create a new Movie with provided ID.
     *
     * @param movieId The movie ID
     */
    public Movie(BigInteger movieId) {
        this.movieId = movieId;
    }

    /**
     * Return the movie ID
     *
     * @return The movie ID
     */
    public BigInteger getMovieId() {
        return movieId;
    }

    /**
     * Set the movie ID
     *
     * @param movieId The movie ID
     */
    public void setMovieId(BigInteger movieId) {
        this.movieId = movieId;
    }

    /**
     * Return the title.
     *
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title.
     *
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Return the release date.
     *
     * @return The release date
     */
    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * Set the release date.
     *
     * @param releaseDate The release date
     */
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * Return the description.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description.
     *
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Return the MPAA rating.
     *
     * @return The MPAA rating
     */
    public String getMpaaRating() {
        return mpaaRating;
    }

    /**
     * Set the MPAA rating.
     *
     * @param mpaaRating The MPAA rating
     */
    public void setMpaaRating(String mpaaRating) {
        this.mpaaRating = mpaaRating;
    }

    /**
     * Return the duration in minutes.
     *
     * @return The duration
     */
    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    /**
     * Set the duration in minutes.
     *
     * @param durationMinutes The duration
     */
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (movieId != null ? movieId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movie)) {
            return false;
        }
        Movie other = (Movie) object;
        return (this.movieId != null || other.movieId == null) &&
                (this.movieId == null || this.movieId.equals(other.movieId));
    }

    @Override
    public String toString() {
        return "org.jlab.demo.persistence.entity.Movie[ movieId=" + movieId + " ]";
    }
    
}
