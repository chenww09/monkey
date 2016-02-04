package model;

public enum Role {
	ADMIN("admin"), SUBSCRIBER("subscriber");
	private final String role;

	Role(String role) {
		this.role = role;
	}
}
