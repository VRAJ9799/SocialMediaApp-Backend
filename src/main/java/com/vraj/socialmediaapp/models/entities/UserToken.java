package com.vraj.socialmediaapp.models.entities;

import com.vraj.socialmediaapp.models.entities.enums.TokenType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_tokens")
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private TokenType tokenType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expireOn;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "user_fk"))
    private User user;

    public UserToken(String token, TokenType tokenType, Date expireOn, User user) {
        this.token = token;
        this.tokenType = tokenType;
        this.expireOn = expireOn;
        this.user = user;
    }
}
