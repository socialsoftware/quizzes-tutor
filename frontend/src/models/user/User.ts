import { Role } from "./Role";

export default class User {
  public role: Role;
  public name: String;

  public constructor(name: String, role: Role) {
    this.name = name;
    this.role = role;
  }
}
