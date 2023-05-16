package com.songs.inforet;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchDAO extends JpaRepository<Search, Integer>{
    public Search findById(int theSlot);
}
