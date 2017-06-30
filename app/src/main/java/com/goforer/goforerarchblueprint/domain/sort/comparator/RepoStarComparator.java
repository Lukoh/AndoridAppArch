package com.goforer.goforerarchblueprint.domain.sort.comparator;

import com.goforer.goforerarchblueprint.repository.model.data.Repo;

import java.util.Comparator;

public class RepoStarComparator implements Comparator<Repo> {
    public RepoStarComparator() {
    }

        @Override
        public int compare(Repo repo1, Repo repo2) {
        return repo2.getStars() - repo1.getStars();
    }
}
