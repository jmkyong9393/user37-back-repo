package com.aivle.bookapp.specification;

import com.aivle.bookapp.domain.Book;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    public static Specification<Book> search(String searchType, String keyword) {

        return (root, query, cb) -> {   // Specification의 핵심 람다
            // keyword가 없으면 조건 없이 전체 조회
            if (StringUtils.isBlank(keyword)) {
                return cb.conjunction();   // WHERE 1=1 과 동일 (모든 데이터 반환)
            }

            String likePattern = "%" + keyword.trim() + "%";
            String pattern = likePattern.toLowerCase();

            switch (searchType){
                case "title", "author", "publisher", "content", "tags" :
                    return cb.like(cb.lower(root.get(searchType)), pattern);
                default:
                    //searchType == "all"
                    Predicate p1 = cb.like(cb.lower(root.get("title")), pattern);
                    Predicate p2 = cb.like(cb.lower(root.get("author")), pattern);
                    Predicate p3 = cb.like(cb.lower(root.get("publisher")), pattern);
                    Predicate p4 = cb.like(cb.lower(root.get("content")), pattern);
                    Predicate p5 = cb.like(cb.lower(root.get("tags")), pattern);

                    return cb.or(p1, p2, p3, p4, p5);
            }
        };
    }
}
