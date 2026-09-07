export interface CurrentUserDto {
  id: number;
  email: string;
  displayName: string;
  isAdmin: boolean;
  timeZone: string | null;
  locale: string | null;
}

export interface UserDto {
  id: number;
  email: string;
  displayName: string;
  isAdmin: boolean;
  createdAt: string;
}
