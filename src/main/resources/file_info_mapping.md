DELETE /file_info
PUT /file_info 
{}
POST /file_info/fileInfo/_mapping
{
  "properties": {
    "id": {
      "type": "text"
    },
    "name": {
      "type": "text",
      "analyzer": "ik_max_word",
      "search_analyzer": "ik_max_word"
    },
    "content": {
      "type": "text",
      "analyzer": "ik_max_word",
      "search_analyzer": "ik_max_word"
    }
  }
}

GET /file_info/
GET /file_info/fileInfo/_search
GET /file_info/_analyze
{
  "analyzer": "standard",
  "text": ["新建 Microsoft PowerPoint 演示文稿.pptx"]
}