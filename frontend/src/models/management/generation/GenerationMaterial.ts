export default class GenerationMaterial {
  id!: string;
  filename!: string;
  status!: string;
  chunkCount: number | null = null;
  parser: string | null = null;
  parseSeconds: number | null = null;
  error: string | null = null;

  constructor(jsonObj?: GenerationMaterial) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.filename = jsonObj.filename;
      this.status = jsonObj.status;
      this.chunkCount = jsonObj.chunkCount;
      this.parser = jsonObj.parser;
      this.parseSeconds = jsonObj.parseSeconds;
      this.error = jsonObj.error;
    }
  }

  static headers = [
    { title: 'File', key: 'filename', align: 'start' },
    { title: 'Status', key: 'status', align: 'center', width: '140px' },
    { title: 'Chunks', key: 'chunkCount', align: 'center', width: '110px' },
    { title: 'Read with', key: 'parser', align: 'center', width: '140px' },
    { title: 'Time (s)', key: 'parseSeconds', align: 'center', width: '110px' },
    { title: 'Actions', key: 'actions', align: 'center', width: '110px', sortable: false },
  ];

  isReady() {
    return this.status === 'READY';
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
