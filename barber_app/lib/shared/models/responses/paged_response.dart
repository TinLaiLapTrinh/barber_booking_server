class PagedResponse<T> {
  final List<T> content;
  final PageMetadata page;

  PagedResponse({
    required this.content,
    required this.page,
  });

  factory PagedResponse.fromJson(
    Map<String, dynamic> json,
    T Function(Map<String, dynamic>) fromJsonT,
  ) {
    return PagedResponse<T>(
      content: (json['content'] as List<dynamic>)
          .map((e) => fromJsonT(e as Map<String, dynamic>))
          .toList(),
      page: PageMetadata.fromJson(json['page'] as Map<String, dynamic>),
    );
  }
}

class PageMetadata {
  final int size;
  final int number;
  final int totalElements;
  final int totalPages;

  PageMetadata({
    required this.size,
    required this.number,
    required this.totalElements,
    required this.totalPages,
  });

  factory PageMetadata.fromJson(Map<String, dynamic> json) {
    return PageMetadata(
      size: json['size'] as int,
      number: json['number'] as int,
      totalElements: json['totalElements'] as int,
      totalPages: json['totalPages'] as int,
    );
  }
}