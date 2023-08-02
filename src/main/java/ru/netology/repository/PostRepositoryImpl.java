package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final List<Post> posts = Collections.synchronizedList(new ArrayList<>());
    private AtomicLong count = new AtomicLong(1);

    @Override
    public List<Post> all() {
        return posts.stream()
                .filter(post -> !post.isRemoved())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Post> getById(long id) {
        Post p = findPostById(id);
        return p != null ? Optional.of(p) : Optional.empty();
    }

    @Override
    public Post save(Post post) {
        if (post.getId() == 0) {
            post.setId(count.get());
            count.getAndIncrement();
            posts.add(post);
            return post;
        }

        Post p = findPostById(post.getId());
        if (p != null) {
            posts.set(posts.indexOf(p), post);
        }
        return p;
    }

    @Override
    public boolean removeById(long id) {
        Post p = findPostById(id);
        if (p != null) {
            p.setRemoved(true);
            posts.set(posts.indexOf(p), p);
            return true;
        }
        return false;
    }

    private Post findPostById(long id) {
        return posts.stream()
                .filter(p -> p.getId() == id && !p.isRemoved())
                .findFirst()
                .orElse(null);
    }
}