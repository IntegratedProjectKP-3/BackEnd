package com.itbangmodkradankanbanapi.database1.DTO;

import com.itbangmodkradankanbanapi.database1.entities.Board;
import com.itbangmodkradankanbanapi.database1.entities.Invite;
import lombok.Data;

import java.util.List;

@Data
public class AccessBoard {
    List<Board> boards;
    List<Board> invites;
}
