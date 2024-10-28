package com.example.analytics.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserStatisticsDTO {



        private long followedPosts;
        private long downvotes;
        private long upvotes;
        private long answers;
        private long posts;




}
