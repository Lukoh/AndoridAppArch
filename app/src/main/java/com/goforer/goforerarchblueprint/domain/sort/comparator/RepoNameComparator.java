package com.goforer.goforerarchblueprint.domain.sort.comparator;

import com.goforer.goforerarchblueprint.repository.model.data.Repo;

import java.util.Comparator;

public class RepoNameComparator implements Comparator<Repo> {
    public RepoNameComparator() {
    }

    @Override
    public int compare(Repo repo1, Repo repo2) {
        String name1 = repo1.getName().toUpperCase();
        String name2 = repo2.getName().toUpperCase();

        //ascending order
        return name1.compareTo(name2);

    }
}
