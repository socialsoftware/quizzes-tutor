export default class GenerationMaterial {
  id!: string;
  filename!: string;
  status!: string;
  chunkCount: number | null = null;
  parser: string | null = null;
  parseSeconds: number | null = null;
  error: string | null = null;
  // How many of its pieces are under a topic; only those are used to generate questions
  placedChunks: number = 0;

  constructor(jsonObj?: GenerationMaterial) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.filename = jsonObj.filename;
      this.status = jsonObj.status;
      this.chunkCount = jsonObj.chunkCount;
      this.parser = jsonObj.parser;
      this.parseSeconds = jsonObj.parseSeconds;
      this.error = jsonObj.error;
      this.placedChunks = jsonObj.placedChunks ?? 0;
    }
  }

  static headers = [
    { title: 'File', key: 'filename', align: 'start' },
    { title: 'Status', key: 'status', align: 'center', width: '140px' },
    { title: 'Under topics', key: 'placedChunks', align: 'center', width: '130px' },
    { title: 'Read with', key: 'parser', align: 'center', width: '140px' },
    { title: 'Time (s)', key: 'parseSeconds', align: 'center', width: '110px' },
    { title: 'Actions', key: 'actions', align: 'center', width: '110px', sortable: false },
  ];

  isReady() {
    return this.status === 'READY';
  }

  // Pieces of a READY material that no topic has yet: the teacher still has to distribute it
  isUndistributed() {
    return this.isReady() && this.placedChunks === 0 && (this.chunkCount ?? 0) > 0;
  }

  isProcessing() {
    return this.status === 'PROCESSING';
  }

  getStatusColor() {
    switch (this.status) {
      case 'READY':
        return 'green';
      case 'FAILED':
        return 'red';
      default:
        return 'blue';
    }
  }
}
