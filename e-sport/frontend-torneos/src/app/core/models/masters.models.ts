export interface Category {
  id: string;
  name: string;
  active: boolean;
}

export interface CreateCategoryRequest {
  name: string;
}

export interface UpdateCategoryRequest {
  name: string;
}

export interface GameType {
  id: string;
  name: string;
  active: boolean;
}

export interface CreateGameTypeRequest {
  name: string;
}

export interface UpdateGameTypeRequest {
  name: string;
}