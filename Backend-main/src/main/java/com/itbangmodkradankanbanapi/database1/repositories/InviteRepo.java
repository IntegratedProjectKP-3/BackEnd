package com.itbangmodkradankanbanapi.database1.repositories;

import com.itbangmodkradankanbanapi.database1.entities.Board;
import com.itbangmodkradankanbanapi.database1.entities.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InviteRepo extends JpaRepository<Invite, String> {
    Invite findByEmail(String email);
    Invite findByName(String name);
    Invite findByOid(String board);
    Invite findByNameAndBoardId(String name ,String BoardId);
    Invite findByEmailAndBoardId(String email,String board);
    List<Invite> findAllByBoardId(String boardId);
    List<Invite> findAllByOid(String oid);
    Invite findByBoardIdAndOid(String boardId, String oid);
}
