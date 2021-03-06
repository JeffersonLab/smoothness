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
@Table(name = "MOVIE", schema = "SUPPORT")
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

    public Movie() {
    }

    public Movie(BigInteger movieId) {
        this.movieId = movieId;
    }

    public BigInteger getMovieId() {
        return movieId;
    }

    public void setMovieId(BigInteger movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMpaaRating() {
        return mpaaRating;
    }

    public void setMpaaRating(String mpaaRating) {
        this.mpaaRating = mpaaRating;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

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
