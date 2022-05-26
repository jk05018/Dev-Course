package com.example.jwt.user;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
@Table(name = "groups")
public class Group {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@OneToMany(mappedBy = "group")
	private List<GroupPermission> permissions = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<GrantedAuthority> getAuthorities(){
		return permissions.stream()
			.map(gp -> new SimpleGrantedAuthority(gp.getPermission().getName()))
			.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return String.format("Group{id=%d, name='%s', permissions=%s}", id, name, getAuthorities());
	}
}
